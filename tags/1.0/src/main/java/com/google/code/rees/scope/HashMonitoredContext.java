package com.google.code.rees.scope;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A Hash implementation of the {@link MonitoredContext}. Resets its
 * own remaining time whenever the wrapped {@link HashMap} is accessed.
 * 
 * @see {@link com.google.code.rees.scope.conversation.MonitoredConversationContextFactory
 *      MonitoredConversationContextFactory}
 * 
 * @author rees.byars
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

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
    @Override
    public V get(Object key) {
        this.ping();
        return super.get(key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public V put(K key, V value) {
        this.ping();
        return super.put(key, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        this.ping();
        super.putAll(m);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<V> values() {
        this.ping();
        return super.values();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public V remove(Object key) {
        this.ping();
        return super.remove(key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        this.ping();
        return super.entrySet();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<K> keySet() {
        this.ping();
        return super.keySet();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getRemainingTime() {
        long remainingTime = (this.timeOfMostRecentAccess + this.duration)
                - System.currentTimeMillis();
        return remainingTime;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reset() {
        this.ping();
    }

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
    @Override
    public void destroy() {
        this.selfMonitor.cancel();
        try {
            this.sessionContext.remove(this.monitoredContextId);
        } catch (IllegalStateException e) {
            LOG.debug("Cannot remove context from session as session has ended.");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TimerTask getTimerTask() {
        return this.selfMonitor;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMonitoredContextId() {
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

    /**
     * Checks to see if the context is still {@link MonitoredContext#isActive()
     * active}.
     * If the context is no longer active, calls
     * {@link MonitoredContext#destroy() context.destroy()}.
     */
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
