package com.foxxy.git.aspect;

import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import com.foxxy.git.annotation.CacheEvict;
import com.foxxy.git.annotation.CachePut;
import com.foxxy.git.annotation.Cacheable;
import com.foxxy.git.cache.CacheLevel;
import com.foxxy.git.cache.CacheProxy.ValueWrapper;
import com.foxxy.git.cache.ConcurrentLimitedHashMap;
import com.foxxy.git.cache.DefaultCacheProxy;

/**
 * 缓存注解拦截的切面，根据不同的注解执行不同的逻辑<br>
 * 〈功能详细描述〉
 *
 * @author 15050977 xy
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
@Aspect
@Component
public class CacheAspectJ {

    private static Logger logger = LoggerFactory.getLogger(CacheAspectJ.class);

    private static Map<String, String[]> parameterNameCaches = new ConcurrentLimitedHashMap<String, String[]>(200);
    /**
     * 缓存SPEL Expression ,最大缓存100个，防止内存占用过多
     */
    private static Map<String, Expression> spelExpressionCaches = new ConcurrentLimitedHashMap<String, Expression>(200);

    private static ExpressionParser parser = new SpelExpressionParser();

    private static LocalVariableTableParameterNameDiscoverer parameterNameDiscovere = new LocalVariableTableParameterNameDiscoverer();

    @Around("@annotation(annotation)")
    public Object advice(ProceedingJoinPoint joinPoint, Cacheable annotation) throws Throwable {
        String[] values = executeTemplate(joinPoint, annotation.keyExpression());
        String key = MessageFormat.format(annotation.keyTemplate(), toObject(values));
        ValueWrapper<?> wrapper = DefaultCacheProxy.proxy().get(annotation.level(), annotation.serializeClass(),
                annotation.name(), key);
        // 如果缓存不为空就直接
        if (null != wrapper.getValue()) {
            // 不是来自一级缓存需要把取到的值放到一级缓存，且不需要广播
            if (!wrapper.getCacheLevel().equals(CacheLevel.FIRST) && CacheLevel.ALL.equals(annotation.level())) {
                // 不失效
                if (0 == annotation.expireTime()) {
                    DefaultCacheProxy.proxy().setFirstNotBroad(annotation.name(), key, wrapper.getValue());
                } else {
                    DefaultCacheProxy.proxy().setFirstNotBroadExpire(annotation.name(), key, wrapper.getValue(),
                            annotation.expireTime());
                }
            }
            return wrapper.getValue();
        }
        // 执行方法从其它设备中获取
        Object result = null;
        try {
            result = joinPoint.proceed();
            // 缓存没有获取到直接放redis和一级缓存中，且一级缓存时不需要广播的
            if (null != result) {
                if (0 == annotation.expireTime()) {
                    // 如果缓存等级是All，则需要把返回值一级和二级缓存都放入
                    if (CacheLevel.ALL.equals(annotation.level())) {
                        DefaultCacheProxy.proxy().setFirstNotBroad(annotation.name(), key, wrapper.getValue());
                        DefaultCacheProxy.proxy().set(CacheLevel.SENCOND, annotation.name(), key, wrapper.getValue());
                    } else if (CacheLevel.SENCOND.equals(annotation.level())) {
                        DefaultCacheProxy.proxy().set(CacheLevel.SENCOND, annotation.name(), key, wrapper.getValue());
                    } else if (CacheLevel.FIRST.equals(annotation.level())) {
                        DefaultCacheProxy.proxy().setFirstNotBroad(annotation.name(), key, wrapper.getValue());
                    }
                } else {
                    // 如果缓存等级是All，则需要把返回值一级和二级缓存都放入，且是待失效时间
                    if (CacheLevel.ALL.equals(annotation.level())) {
                        DefaultCacheProxy.proxy().setFirstNotBroadExpire(annotation.name(), key, wrapper.getValue(),
                                annotation.expireTime());
                        DefaultCacheProxy.proxy().setonExpire(CacheLevel.SENCOND, annotation.name(), key,
                                wrapper.getValue(), annotation.expireTime());
                    } else if (CacheLevel.SENCOND.equals(annotation.level())) {
                        DefaultCacheProxy.proxy().setonExpire(CacheLevel.SENCOND, annotation.name(), key,
                                wrapper.getValue(), annotation.expireTime());
                    } else if (CacheLevel.FIRST.equals(annotation.level())) {
                        DefaultCacheProxy.proxy().setFirstNotBroadExpire(annotation.name(), key, wrapper.getValue(),
                                annotation.expireTime());
                    }
                }
            }
        } catch (Exception e) {
            String targetName = joinPoint.getTarget().getClass().getName();
            String methodName = joinPoint.getSignature().getName();
            logger.error("invoke className:{} method failed methodName{}:", new Object[] { targetName, methodName }, e);
            throw new RuntimeException("invoke method failed:" + methodName, e);
        }
        return result;
    }

    @Around("@annotation(annotation)")
    public Object advice(ProceedingJoinPoint joinPoint, CachePut annotation) throws Throwable {
        String[] values = executeTemplate(joinPoint, annotation.keyExpression());
        String key = MessageFormat.format(annotation.keyTemplate(), toObject(values));
        Object result = null;
        try {

            result = joinPoint.proceed();
        } catch (Exception e) {
            String targetName = joinPoint.getTarget().getClass().getName();
            String methodName = joinPoint.getSignature().getName();
            logger.error("invoke className:{} method failed methodName{}:", new Object[] { targetName, methodName }, e);
            throw new RuntimeException("invoke method failed:" + methodName, e);
        }
        // 取到的不是空此时需要更新缓存
        if (null != result) {
            // 不失效
            if (0 == annotation.expireTime()) {
                DefaultCacheProxy.proxy().set(annotation.level(), annotation.name(), key, result);
            } else {
                DefaultCacheProxy.proxy().setonExpire(annotation.level(), annotation.name(), key, result,
                        annotation.expireTime());
            }
        }
        return result;
    }

    @Around("@annotation(annotation)")
    public Object advice(ProceedingJoinPoint joinPoint, CacheEvict annotation) throws Throwable {
        String[] values = executeTemplate(joinPoint, annotation.keyExpression());
        String key = MessageFormat.format(annotation.keyTemplate(), toObject(values));
        Object result = null;
        try {
            result = joinPoint.proceed();
        } catch (Exception e) {
            String targetName = joinPoint.getTarget().getClass().getName();
            String methodName = joinPoint.getSignature().getName();
            logger.error("invoke className:{} method failed methodName{}:", new Object[] { targetName, methodName }, e);
            throw new RuntimeException("invoke method failed:" + methodName, e);
        }
        // 从缓存移除
        DefaultCacheProxy.proxy().evict(annotation.level(), annotation.name(), key);
        return result;
    }

    /**
     * 功能描述: 根据指定表达式获取对应的值 〈功能详细描述〉
     *
     * @param template 模板id
     * @param joinPoint 连接点参数
     * @return
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选)
     */
    private String[] executeTemplate(ProceedingJoinPoint joinPoint, String... templates) {
        if (ArrayUtils.isEmpty(templates)) {
            return ArrayUtils.EMPTY_STRING_ARRAY;
        }
        String[] values = new String[templates.length];
        // get method parameter name
        String methodLongName = joinPoint.getSignature().toLongString();
        String[] parameterNames = parameterNameCaches.get(methodLongName);
        if (parameterNames == null) {
            Method method = getMethod(joinPoint);
            parameterNames = parameterNameDiscovere.getParameterNames(method);
            parameterNameCaches.put(methodLongName, parameterNames);
        }

        // add args to expression context
        StandardEvaluationContext context = new StandardEvaluationContext();
        Object[] args = joinPoint.getArgs();
        if (args.length == parameterNames.length) {
            for (int i = 0, len = args.length; i < len; i++)
                context.setVariable(parameterNames[i], args[i]);
        }
        int index = 0;
        // 循环执行表达式
        for (String template : templates) {
            // cacha expression
            Expression expression = spelExpressionCaches.get(template);
            if (expression == null) {
                expression = parser.parseExpression(template);
                spelExpressionCaches.put(template, expression);
            }
            String value = expression.getValue(context, String.class);
            values[index] = value;
            index++;
        }
        return values;
    }

    private Method getMethod(ProceedingJoinPoint joinPoint) {
        String methodLongName = joinPoint.getSignature().toLongString();
        Method[] methods = joinPoint.getTarget().getClass().getMethods();
        Method method = null;
        for (int i = 0, len = methods.length; i < len; i++) {
            if (methodLongName.equals(methods[i].toString())) {
                method = methods[i];
                break;
            }
        }
        return method;
    }

    private Object[] toObject(String[] strs) {
        Object[] objs = new Object[strs.length];
        int index = 0;
        for (String str : strs) {
            objs[index] = str;
            index++;
        }
        return objs;
    }
}
