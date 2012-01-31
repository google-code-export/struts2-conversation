package com.google.code.rees.scope;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class HashMonitoredContext<K, V> extends HashMap<K, V> implements MonitoredContext<K, V> {

	private static final long serialVersionUID = 5810194340880306239L;
	
	protected long timeOfMostRecentAccess;
	protected String sessionId;
	protected Map<String, Object> sessionContext;
	protected long duration;
	protected ContextTimerTask selfMonitor;
	
	@Override
	public void init(String sessionId, Map<String, Object> sessionContext, long duration) {
		this.sessionId = sessionId;
		this.sessionContext = sessionContext;
		this.duration = duration;
		this.sessionContext.put(sessionId, this);
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
		long remainingTime = (this.timeOfMostRecentAccess + this.duration) - System.currentTimeMillis();
		return remainingTime;
	}

	@Override
	public void reset() {
		this.ping();
	}

	@Override
	public boolean isActive() {
		return this.sessionContext.containsKey(this.sessionId) && this.getRemainingTime() > 0;
	}
	
	@Override
	public void destroy() {
		this.selfMonitor.cancel();
		this.sessionContext.remove(this.sessionId);
	}
	
	@Override
	public TimerTask getTimerTask() {
		return this.selfMonitor;
	}
	
	@Override
	public String getSessionKey() {
		return this.sessionId;
	}
	
	protected void ping() {
		this.timeOfMostRecentAccess = System.currentTimeMillis();
	}
	
}

class ContextTimerTask extends TimerTask {
	
	private static final Logger LOG = LoggerFactory.getLogger(HashMonitoredContext.class);
	private HashMonitoredContext<?,?> context;
	
	public ContextTimerTask(HashMonitoredContext<?,?> abstractMonitorContext) {
		this.context = abstractMonitorContext;
	}

	@Override
	public void run() {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Monitoring context with ID " + this.context.sessionId);
		}
		if (!this.context.isActive()) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("Destroying context with ID " + this.context.sessionId);
			}
			context.destroy();
		}
	}
	
}

