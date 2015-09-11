package com.foxxy.git.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.foxxy.git.cache.CacheLevel;

/**
 * 该注解标注之后，先从缓存取，取不到再从db中取，并将db中的取的数据放到缓存，放缓存时会根据设定时间设置对应的有效期 <br>
 * 〈功能详细描述〉
 *
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Cacheable {

    /**
     * 
     * 功能描述: 缓存名称 〈功能详细描述〉
     *
     * @return
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选)
     */
    String name();

    /**
     * 
     * 功能描述: 缓存管理器名称 〈功能详细描述〉
     *
     * @return
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选)
     */
    String cacheManagerName() default "default";

    /**
     * 
     * 功能描述: 缓存key模板，如"ord:{0}:{1}" 〈功能详细描述〉
     *
     * @return
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选)
     */
    String keyTemplate() default "";

    /**
     * 
     * 功能描述: 缓存key表达式，如{"#order.orderUserId","#order.orderId"}<br>
     * 〈功能详细描述〉
     *
     * @return
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选)
     */
    String[] keyExpression() default "";

    /**
     * 
     * 功能描述: 默认0不失效，单位秒 〈功能详细描述〉
     *
     * @return
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选)
     */
    int expireTime() default 0;

    /**
     * 
     * 功能描述: 缓存等级 〈功能详细描述〉
     *
     * @return
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选)
     */
    CacheLevel level() default CacheLevel.ALL;

    /**
     * 
     * 功能描述: 缓存中的数据用什么类型反序列化出来<br>
     * 〈功能详细描述〉
     *
     * @return
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选)
     */
    Class<?> serializeClass() default Object.class;
}
