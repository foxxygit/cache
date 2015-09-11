package com.foxxy.git.spring;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.store.MemoryStoreEvictionPolicy;

import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.xml.AbstractSimpleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

/**
 * 〈一句话功能简述〉解析cache 标签并将cache注册到spring容器中 〈功能详细描述〉
 *
 * @author 15050977 xy
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class CacheBeanDefinitionParser extends AbstractSimpleBeanDefinitionParser {

    /**
     * element 相当于对应的element元素 parserContext 解析的上下文 builder 用于该标签的实现
     */
    @Override
    protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {

        // 从标签中取出对应的属性值
        String name = element.getAttribute("name").trim();
        // 从标签中取出对应的属性值
        String id = element.getAttribute("id").trim();
        // the maximum number of elements in memory, before they are evicted (0 == no limit)
        Integer maxElementsInMemory = Integer.valueOf(element.getAttribute("maxElementsInMemory"));
        // Element是否永久有效，一但设置了，timeout将不起作用。
        boolean eternal = Boolean.valueOf(element.getAttribute("eternal"));
        // 设置Element在失效前的允许闲置时间。仅当element不是永久有效时使用，可选属性，默认值是0，也就是可闲置时间无穷大。
        long timeToIdleSeconds = Long.valueOf(element.getAttribute("timeToIdleSeconds"));
        // 设置Element在失效前允许存活时间。最大时间介于创建时间和失效时间之间。仅当element不是永久有效时使用，默认是0.，也就是element存活时间无穷大。
        long timeToLiveSeconds = Long.valueOf(element.getAttribute("timeToLiveSeconds"));
        // 磁盘失效线程运行时间间隔，默认是120秒。
        long diskExpiryThreadIntervalSeconds = Long.valueOf(element.getAttribute("diskExpiryThreadIntervalSeconds"));
        // 当达到maxElementsInMemory限制时，Ehcache将会根据指定的策略去清理内存。默认策略是LRU（最近最少使用）。你可以设置为FIFO（先进先出）或是LFU（较少使用）
        String memoryStoreEvictionPolicy = element.getAttribute("memoryStoreEvictionPolicy").trim();
        // 配置此属性，当内存中Element数量达到maxElementsInMemory时，Ehcache将会Element写到磁盘中。
        boolean overflowToDisk = Boolean.valueOf(element.getAttribute("overflowToDisk"));

        String beanName = element.getAttribute("broadcastListener").trim();
        // 获取泛型声明的类用来做反序列化操作
        String awareClassName = element.getAttribute("serializerAwareClassName") == null ? null : element.getAttribute(
                "serializerAwareClassName").trim();
        boolean statistics = Boolean.valueOf(element.getAttribute("statistics"));
        // 这里是单例，不会出现多个
        EhCacheWrapperHolder holder = EhCacheWrapperHolder.getInstance();

        net.sf.ehcache.Cache cache = new Cache(name, maxElementsInMemory,
                buildEvictionPolicy(memoryStoreEvictionPolicy), overflowToDisk, null, eternal, timeToLiveSeconds,
                timeToIdleSeconds, overflowToDisk, diskExpiryThreadIntervalSeconds, null);
        // 注册到ehcache的实例框架中去
        CacheManager.getInstance().addCache(cache);
        cache.setStatisticsEnabled(statistics);
        holder.putIfAbsent(name, new CacheWrapper(cache, beanName, awareClassName));
        builder.addPropertyValue("cache", cache);
        builder.addPropertyReference("listener", beanName);
        // Spring框架必须要求有一个id属性
        // 把bean注入到spring容器中
        BeanDefinitionReaderUtils.registerBeanDefinition(new BeanDefinitionHolder(builder.getRawBeanDefinition(), id),
                parserContext.getRegistry());
        // 把bean注入到spring容器中
        BeanDefinitionReaderUtils.registerBeanDefinition(
                new BeanDefinitionHolder(builder.getRawBeanDefinition(), name), parserContext.getRegistry());

    }

    private MemoryStoreEvictionPolicy buildEvictionPolicy(String policy) {
        return MemoryStoreEvictionPolicy.fromString(policy);
    }

    @Override
    protected Class<CacheWrapper> getBeanClass(Element element) {
        // 返回该标签所定义的类实现,在这里是为了创建出CacheWrapper对象
        return CacheWrapper.class;
    }
}
