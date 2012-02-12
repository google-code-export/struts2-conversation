package com.google.code.rees.scope;

import java.io.Serializable;
import java.util.Map;
import java.util.TimerTask;

/**
 * A serializable map that can be monitored for activity and
 * destroyed when no longer active.
 * 
 * @author rees.byars
 */
public interface MonitoredContext<K, V> extends Map<K, V>, Serializable {

    /**
     * Initialize the context with the given parameters. The
     * monitoredContextId should be the key by which this context
     * is retrieved from the sessionContext. The duration is the period
     * of inactivity after which this context should be destroyed.
     * 
     * @param monitoredContextId
     * @param sessionContext
     * @param duration
     */
    public void init(String monitoredContextId,
            Map<String, Object> sessionContext, long duration);

    /**
     * Get the context's id
     * 
     * @return
     */
    public String getMonitoredContextId();

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
     * Destroys the context removing it from its parent session context
     */
    public void destroy();

    /**
     * the remaining time
     * 
     * @return
     */
    public long getRemainingTime();

    /**
     * A self-monitor that can be registered with a timer to have
     * this context monitor itself for destruction
     * 
     * @return
     */
    public TimerTask getTimerTask();

}
