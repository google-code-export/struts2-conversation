package com.google.code.rees.scope;

import java.io.Serializable;
import java.util.Map;
import java.util.TimerTask;

public interface MonitoredContext<K, V> extends Map<K, V>, Serializable {

    public void init(String monitoredContextId,
            Map<String, Object> sessionContext, long duration);

    public String getSessionKey();

    public void reset();

    public boolean isActive();

    public void destroy();

    public long getRemainingTime();

    public TimerTask getTimerTask();

}
