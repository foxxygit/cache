package com.foxxy.git.cache;

import java.io.Serializable;

/**
 * 
 * 对需要广播的元素包装广播出去<br>
 * 〈功能详细描述〉
 *
 * @author 15050977 xy
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class ElementWrapper implements Serializable {

    private static final long serialVersionUID = -3842809913590744804L;

    /**
     * 事件触发的key 比如DELETE，UPDTA，ADD
     */
    private ActionEventKey actionEventKey;

    /**
     * 缓存管理器名称
     */
    private String managerName;
    /**
     * 缓存名称
     */
    private String cacheName;
    /**
     * 需要广播的key
     */
    private String key;

    /**
     * 需要广播的值
     */
    private Object value;

    /**
     * 对所有缓存的元素有一个md5编码值，来记录改记录是否修改过
     */
    private String md5Code;

    /**
     * 存活时间
     */
    private Integer liveSeconds;

    public ElementWrapper(ActionEventKey actionEventKey, String managerName, String cacheName, String key,
            Object value, String md5Code, Integer liveSeconds) {
        super();
        this.managerName = managerName;
        this.cacheName = cacheName;
        this.actionEventKey = actionEventKey;
        this.key = key;
        this.value = value;
        this.md5Code = md5Code;
        this.liveSeconds = liveSeconds;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getMd5Code() {
        return md5Code;
    }

    public void setMd5Code(String md5Code) {
        this.md5Code = md5Code;
    }

    public ActionEventKey getActionEventKey() {
        return actionEventKey;
    }

    public void setActionEventKey(ActionEventKey actionEventKey) {
        this.actionEventKey = actionEventKey;
    }

    public Integer getLiveSeconds() {
        return liveSeconds;
    }

    public void setLiveSeconds(Integer liveSeconds) {
        this.liveSeconds = liveSeconds;
    }

    public String getCacheName() {
        return cacheName;
    }

    public void setCacheName(String cacheName) {
        this.cacheName = cacheName;
    }

    public String getManagerName() {
        return managerName;
    }

    public void setManagerName(String managerName) {
        this.managerName = managerName;
    }
}
