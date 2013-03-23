/*******************************************************************************
 *
 *  Struts2-Conversation-Plugin - An Open Source Conversation- and Flow-Scope Solution for Struts2-based Applications
 *  =================================================================================================================
 *
 *  Copyright (C) 2012 by Rees Byars
 *  http://code.google.com/p/struts2-conversation/
 *
 * **********************************************************************************************************************
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 *  the License. You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 *  an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 *  specific language governing permissions and limitations under the License.
 *
 * **********************************************************************************************************************
 *
 *  $Id: HashMonitoredContext.java reesbyars $
 ******************************************************************************/
package com.github.overengineer.scope.monitor;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * A Serializable, thread-safe Hash implementation of the Map and Timeoutable interfaces.
 * Resets its own remaining time whenever the underlying {@link Hashtable} is
 * accessed.  Extends Hashtable for thread-safety without the heap-overhead of a
 * ConcurrentHashMap - reasonable since scalable concurrency is not much concern,
 * but scalable heap usage is.
 *
 * @author rees.byars
 */
public abstract class HashMonitoredContext<K, V, T extends MonitoredContext<K, V, T>> extends Hashtable<K, V> implements MonitoredContext<K, V, T> {

    private static final long serialVersionUID = 5810194340880306239L;

    //hashtable settings geared toward saving a little space - users aren't likely to access a context massively concurrently anyway
    private static final int INITIAL_CAPACITY = 8;
    private static final float LOAD_FACTOR = .9f;

    protected Collection<TimeoutListener<T>> timeoutListeners = new ConcurrentLinkedQueue<TimeoutListener<T>>();
    protected long timeOfMostRecentAccess;
    protected long maxIdleTime;

    public HashMonitoredContext(long maxIdleTime) {
        super(INITIAL_CAPACITY, LOAD_FACTOR);
        this.maxIdleTime = maxIdleTime;
        ping();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public V get(Object key) {
        ping();
        return super.get(key);
    }

    /**
     * TODO
     */
    @Override
    public V put(K key, V value) {

        ping();

        //null keys not allowed in hashtable
        if (key == null) {
            return null;
        }

        //null values not allowed in hashtable, so we simulate by removing any current value
        if (value == null) {
            super.remove(key);
            return null;
        }

        return super.put(key, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        ping();
        super.putAll(m);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<V> values() {
        ping();
        return super.values();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public V remove(Object key) {
        ping();
        return super.remove(key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        ping();
        return super.entrySet();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<K> keySet() {
        ping();
        return super.keySet();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getRemainingTime() {
        return (this.timeOfMostRecentAccess + this.maxIdleTime) - System.currentTimeMillis();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reset() {
        ping();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setMaxIdleTime(long maxIdleTime) {
        this.maxIdleTime = maxIdleTime;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addTimeoutListener(TimeoutListener<T> timeoutListener) {
        timeoutListeners.add(timeoutListener);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public void timeout() {
        for (TimeoutListener<T> timeoutListener : timeoutListeners) {
            timeoutListener.onTimeout((T) this);
        }
        timeoutListeners.clear();
        clear();
    }

    protected void ping() {
        timeOfMostRecentAccess = System.currentTimeMillis();
    }

}
