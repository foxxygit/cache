package com.foxxy.git.coder;

public interface CacheCoder {

    /**
     * 
     * 功能描述: 编码 〈功能详细描述〉
     *
     * @param obj
     * @return
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选)
     */
    String encode(Object obj);
    
    /**
     * 
     * 功能描述: 解码
     * 〈功能详细描述〉
     *
     * @param b
     * @param requireType
     * @return
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选)
     */
    <T> T decode(byte[] b, Class<T> requireType);
    
    
    /**
     * 
     * 功能描述: 解码
     * 〈功能详细描述〉
     *
     * @param b
     * @param requireType
     * @return
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选)
     */
    <T> T decode(String content, Class<T> requireType);
}
