package com.foxxy.git.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.foxxy.git.cache.CacheLevel;


/**
 *  该注解标注之后，将方法放回值放到缓存中 <br> 
 * 〈功能详细描述〉
 *
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface CachePut {
    /**
     * 
     * 功能描述: 缓存名称
     * 〈功能详细描述〉
     *
     * @return
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选)
     */
    String name();
    
    /**
     * 
     * 功能描述: 缓存管理器名称
     * 〈功能详细描述〉
     *
     * @return
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选)
     */
    String cacheManagerName() default "default";
    
    /**
     * 
     * 功能描述: 缓存key模板格式，如"ord:{0}:{1}"
     * 〈功能详细描述〉
     *
     * @return
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选)
     */
    String keyTemplate() default "";
    
    /**
     * 
     * 功能描述: 缓存key表达式，如{"#order.orderUserId","#order.orderId"}
     * 〈功能详细描述〉
     *
     * @return
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选)
     */
    String[] keyExpression() default "";
    
    /**
     * 
     * 功能描述: 默认0不失效 单位秒
     * 〈功能详细描述〉
     *
     * @return
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选)
     */
    int expireTime() default 0;
    
    /**
     * 
     * 功能描述: 缓存等级
     * 〈功能详细描述〉
     *
     * @return
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选)
     */
    CacheLevel level() default CacheLevel.ALL;
}
