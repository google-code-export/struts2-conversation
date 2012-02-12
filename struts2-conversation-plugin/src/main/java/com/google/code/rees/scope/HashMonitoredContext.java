package com.google.code.rees.scope;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author rees.byars
 * 
 */
public class HashMonitoredContext<K, V> extends HashMap<K, V> implements
        MonitoredContext<K, V> {

    private static final long serialVersionUID = 5810194340880306239L;
    private static final Logger LOG = LoggerFactory
            .getLogger(HashMonitoredContext.class);

    protected long timeOfMostRecentAccess;
    protected String monitoredContextId;
    protected Map<String, Object> sessionContext;
    protected long duration;
    protected ContextTimerTask selfMonitor;

    @Override
    public void init(String monitoredContextId,
            Map<String, Object> sessionContext, long duration) {
        this.monitoredContextId = monitoredContextId;
        this.sessionContext = sessionContext;
        this.duration = duration;
        this.sessionContext.put(monitoredContextId, this);
        this.selfMonitor = new ContextTimerTask(this);
        this.ping();
    }

    @Override
    public V get(Object key) {
        this.ping();
        return super.get(key);
    }

    @Override
    public V put(K key, V value) {
        this.ping();
        return super.put(key, value);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        this.ping();
        super.putAll(m);
    }

    @Override
    public Collection<V> values() {
        this.ping();
        return super.values();
    }

    @Override
    public V remove(Object key) {
        this.ping();
        return super.remove(key);
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        this.ping();
        return super.entrySet();
    }

    @Override
    public Set<K> keySet() {
        this.ping();
        return super.keySet();
    }

    @Override
    public long getRemainingTime() {
        long remainingTime = (this.timeOfMostRecentAccess + this.duration)
                - System.currentTimeMillis();
        return remainingTime;
    }

    @Override
    public void reset() {
        this.ping();
    }

    @Override
    public boolean isActive() {
        boolean inSession;
        try {
            inSession = this.sessionContext
                    .containsKey(this.monitoredContextId);
        } catch (IllegalStateException e) {
            LOG.debug("Session has ended.");
            inSession = false;
        }
        return inSession && this.getRemainingTime() > 0;
    }

    @Override
    public void destroy() {
        this.selfMonitor.cancel();
        try {
            this.sessionContext.remove(this.monitoredContextId);
        } catch (IllegalStateException e) {
            LOG.debug("Cannot remove context from session as session has ended.");
        }
    }

    @Override
    public TimerTask getTimerTask() {
        return this.selfMonitor;
    }

    @Override
    public String getSessionKey() {
        return this.monitoredContextId;
    }

    protected void ping() {
        this.timeOfMostRecentAccess = System.currentTimeMillis();
    }

}

class ContextTimerTask extends TimerTask {

    private static final Logger LOG = LoggerFactory
            .getLogger(ContextTimerTask.class);
    private HashMonitoredContext<?, ?> context;

    public ContextTimerTask(HashMonitoredContext<?, ?> abstractMonitorContext) {
        this.context = abstractMonitorContext;
    }

    @Override
    public void run() {
        if (LOG.isTraceEnabled()) {
            LOG.trace("Monitoring context with ID "
                    + this.context.monitoredContextId);
        }
        if (!this.context.isActive()) {
            if (LOG.isTraceEnabled()) {
                LOG.trace("Destroying context with ID "
                        + this.context.monitoredContextId);
            }
            context.destroy();
        }
    }

}
