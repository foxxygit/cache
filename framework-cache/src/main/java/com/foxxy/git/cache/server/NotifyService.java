package com.foxxy.git.cache.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.foxxy.git.cache.ElementWrapper;
import com.foxxy.git.monitor.DefaultThreadPoolExecutorService;
import com.foxxy.git.monitor.DefaultThreadPoolMonitorService;
import com.foxxy.git.utils.NetUtils;
import com.foxxy.git.utils.ResourceUtils;

/**
 * 缓存通知服务<br>
 * 〈功能详细描述〉
 *
 * @author 15050977 xy
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
@Service(value = "defaultNotifyService")
public class NotifyService implements InitializingBean {

    private static Logger LOGGER = LoggerFactory.getLogger(NotifyService.class);

    private static final String PROTOCOL = "http://";

    private static final String URL_PREFIX = "/element/notify.do";

    private final Properties nodeProperties = new Properties();

    private static final int DEFAULT_QUEUECAPACITY = 10000;

    private ThreadPoolExecutor taskExecutor = new DefaultThreadPoolExecutorService(5, 5, 2000, DEFAULT_QUEUECAPACITY,
            true, new DefaultThreadPoolMonitorService()).createNewThreadPool("NotifyService");

    private ThreadPoolExecutor retryTaskExecutor = new DefaultThreadPoolExecutorService(5, 5, 2000,
            DEFAULT_QUEUECAPACITY, true, new DefaultThreadPoolMonitorService())
            .createNewThreadPool("NotifyService-retryTaskExecutor");

    private LinkedBlockingQueue<QueueTask> queue = new LinkedBlockingQueue<QueueTask>(1000);

    private static Gson gson = new GsonBuilder().enableComplexMapKeySerialization()
            .setDateFormat("yyyy-MM-dd HH:mm:ss").create();

    private static final Integer RETRY_TIMES = 3;

    private static final Integer TIMEOUT = 5000;

    private Timer timer = new Timer("NotifyService-timer");

    public Properties getNodeProperties() {
        return this.nodeProperties;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        loadNodes();
    }

    private void loadNodes() {
        InputStream in = null;
        try {
            in = ResourceUtils.getResourceAsStream("/conf/node.properties");
            nodeProperties.load(in);
        } catch (IOException e) {
            LOGGER.error("加载节点配置文件失败");
        } finally {

            try {
                if (in != null)
                    in.close();
            } catch (IOException e) {
                LOGGER.error("关闭node.properties失败", e);
            }
        }
        LOGGER.info("节点列表:" + nodeProperties);
        // 启动定时任务
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    if (!queue.isEmpty()) {
                        QueueTask task = queue.take();
                        invokeURL(task.url, task.wrapper);
                    }
                } catch (InterruptedException e) {
                    // donoting
                }
            }
        }, 180000, 30000);// milliseconds 首次启动三分钟后开始执行，间隔30s执行一次
        // 启动一个后台优雅停机的钩子
        addShutdownHook();
    }

    /**
     * 
     * 功能描述:应用程序优雅关闭时需要将队列中的数据全部消耗完 〈功能详细描述〉
     *
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选)
     */
    private void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                timer.cancel();
                while (!queue.isEmpty()) {
                    try {
                        QueueTask task = queue.take();
                        invokeURL(task.url, task.wrapper);
                    } catch (InterruptedException e) {
                        // donoting
                    } catch (Exception e) {
                        // donoting
                    }
                }
            }
        });
    }

    public void notifyElementChange(final ElementWrapper wrapper) {
        Enumeration<?> enu = nodeProperties.propertyNames();
        while (enu.hasMoreElements()) {
            // ip+端口+容器路径
            final String address = (String) enu.nextElement();
            // 如果是本机就直接跳过,如果一个进程部署多个容器就不歇菜
            if (address.contains(NetUtils.LOCAL_IP)) {
                continue;
            }
            final String urlString = generatePullUrl(address);
            Future<Boolean> future = taskExecutor.submit(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    final String result = invokeURL(urlString, wrapper);
                    // 不合法的请求,直接返回true
                    if ("UNKOWN_QEQUEST".equals(result)) {
                        return true;
                    }
                    LOGGER.info("通知节点" + address + "缓存信息改变：" + result);
                    if (!"200".equals(result)) {
                        // 重试
                        doExcuteRetry(urlString, wrapper);
                    }
                    return "200".equals(result);
                }
            });
            try {
                if (!future.get(TIMEOUT, TimeUnit.MILLISECONDS)) {
                    LOGGER.error("通知节点" + address + "缓存信息改变失败" + "key:" + wrapper.getKey());
                    LOGGER.error("wait retrytimes!!!!!!!");
                }
            } catch (InterruptedException e) {
                LOGGER.error("通知节点" + address + "缓存信息改变失败" + "key:" + wrapper.getKey());
                future.cancel(true);
            } catch (ExecutionException e) {
                LOGGER.error("通知节点" + address + "缓存信息改变失败" + "key:" + wrapper.getKey());
                future.cancel(true);
            } catch (TimeoutException e) {
                LOGGER.error("通知节点" + address + "缓存信息改变失败" + "key:" + wrapper.getKey());
                future.cancel(true);
            }
        }
    }

    private void doExcuteRetry(String urlString, ElementWrapper wrapper) {
        retryTaskExecutor.execute(new RetryTask(urlString, wrapper));
    }

    public class QueueTask {
        private String url;
        private ElementWrapper wrapper;

        public QueueTask(String url, ElementWrapper wrapper) {
            super();
            this.url = url;
            this.wrapper = wrapper;
        }

        public String getUrl() {
            return url;
        }

        public ElementWrapper getWrapper() {
            return wrapper;
        }
    }

    public class RetryTask implements Runnable {

        private AtomicInteger counter = new AtomicInteger(0);

        private String url;

        private ElementWrapper wrapper;

        public RetryTask(String url, ElementWrapper wrapper) {
            super();
            this.url = url;
            this.wrapper = wrapper;
        }

        // 重试还失败就放到补偿队列里面 定时再去修复
        @Override
        public void run() {
            while (counter.get() < RETRY_TIMES + 1) {
                try {
                    final String result = invokeURL(url, wrapper);
                    // 404就不要重试了 500内部错误
                    if ("200".equals(result) || "404".equals(result) || "500".equals(result)) {
                        break;
                    }
                    counter.incrementAndGet();
                } catch (Exception e) {
                    counter.incrementAndGet();
                }
            }
            // 重试了之后还是失败就放到队列里面去 等一会再去执行
            if (counter.get() >= RETRY_TIMES) {
                try {
                    queue.put(new QueueTask(url, wrapper));
                } catch (InterruptedException e) {
                    // donoting
                }
            }
        }
    }

    private String invokeURL(String urlString, ElementWrapper wrapper) {
        HttpURLConnection conn = null;
        URL url = null;
        try {
            url = new URL(urlString);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setConnectTimeout(TIMEOUT);
            conn.setReadTimeout(TIMEOUT);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "text/xml;charset=UTF-8");
            conn.connect();
            PrintWriter out = new PrintWriter(new OutputStreamWriter(conn.getOutputStream(), "UTF-8"));
            out.write(gson.toJson(wrapper));
            out.flush();
            out.close();
            InputStream urlStream = conn.getInputStream();
            return IOUtils.toString(urlStream, "UTF-8");
        } catch (Exception e) {
            // TODO 失败类型
            LOGGER.error("http调用失败,url=" + urlString, e);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return "error";
    }

    private String generatePullUrl(String address) {
        StringBuilder result = new StringBuilder();
        result.append(PROTOCOL).append(address).append(URL_PREFIX);
        return result.toString();
    }
}
