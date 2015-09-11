package com.foxxy.git.cache.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.foxxy.git.cache.ActionEventKey;
import com.foxxy.git.cache.CacheBroadcastListener;
import com.foxxy.git.cache.CacheLevel;
import com.foxxy.git.cache.DefaultBroadCastListener;
import com.foxxy.git.cache.ElementWrapper;
import com.foxxy.git.spring.CacheManagerHolder;

public class DispatchNotifyServlet extends HttpServlet {

    private static final long serialVersionUID = -8977160760566477904L;

    private static Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();

    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        try {
            String value = IOUtils.toString(request.getInputStream(), "UTF-8");
            if (StringUtils.isBlank(value)) {
                response.getWriter().write("UNKOWN_QEQUEST");
                return;
            }
            JsonParser parser = new JsonParser();
            JsonObject jsonObject = parser.parse(value).getAsJsonObject();
            String managerName = jsonObject.get("managerName").getAsString();
            String cacheName = jsonObject.get("cacheName").getAsString();
            String key = jsonObject.get("key").getAsString();

            String md5Code = null;
            if (null != jsonObject.get("md5Code")) {
                md5Code = jsonObject.get("md5Code").getAsString();
            }
            int liveSeconds = 0;
            if (null != jsonObject.get("liveSeconds")) {
                liveSeconds = jsonObject.get("liveSeconds").getAsInt();
            }
            // 将data节点下的内容转为JsonArray
            JsonElement jsonElement = jsonObject.get("value");
            JsonElement actionEventKey = jsonObject.get("actionEventKey");

            CacheBroadcastListener listener = CacheManagerHolder.getInstance().get(managerName)
                    .getCache(CacheLevel.FIRST, cacheName).getCacheBroadcastListener();
            Object obj = null;
            // 删除时value可能为空
            if (null != jsonElement) {
                if (listener instanceof DefaultBroadCastListener) {
                    String className = CacheManagerHolder.getInstance().get(managerName)
                            .getCache(CacheLevel.FIRST, cacheName).getSerialAwareClassName();
                    // 映射为类实例
                    obj = gson.fromJson(jsonElement, org.springframework.util.ClassUtils.forName(className, Thread
                            .currentThread().getContextClassLoader()));
                } else {
                    // listener声明的实例
                    obj = gson.fromJson(jsonElement, listener.getAwareSerialClass());
                }

            }
            ElementWrapper wrapper = new ElementWrapper(gson.fromJson(actionEventKey, ActionEventKey.class),
                    managerName, cacheName, key, obj, md5Code, liveSeconds);
            boolean result = listener.notifyElementUpdate(wrapper);
            if (result) {
                response.getWriter().write("200");
                return;
            }

        } catch (Exception e) {
            // return 500;
            response.getWriter().write("500");
            return;
        }
        response.getWriter().write("500");
    }
}
