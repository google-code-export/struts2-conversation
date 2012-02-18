package com.google.code.rees.scope.util;

import java.io.Serializable;
import java.util.Map;

/**
 * A serializable map that can be monitored for activity and
 * destroyed when no longer active.
 * 
 * @author rees.byars
 */
public interface MonitoredContext<K, V> extends Map<K, V>, Serializable {

    /**
     * Get the context's id
     * 
     * @return
     */
    public String getId();

    /**
     * Reset the remaining time
     */
    public void reset();

    /**
     * Returns true if the duration has not expired
     * 
     * @return
     */
    public boolean isActive();

    /**
     * the remaining time
     * 
     * @return
     */
    public long getRemainingTime();

}
