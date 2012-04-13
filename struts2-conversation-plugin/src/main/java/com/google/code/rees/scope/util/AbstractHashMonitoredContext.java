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
 *  $Id: AbstractHashMonitoredContext.java reesbyars $
 ******************************************************************************/
package com.google.code.rees.scope.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * A Hash implementation of the {@link MonitoredContext}. Resets its
 * own remaining time whenever the wrapped {@link HashMap} is accessed.
 * 
 * @see {@link com.google.code.rees.scope.conversation.MonitoredConversationContextFactory
 *      MonitoredConversationContextFactory}
 * 
 * @author rees.byars
 */
public class AbstractHashMonitoredContext<K, V> extends HashMap<K, V> implements
        MonitoredContext<K, V> {

    private static final long serialVersionUID = 5810194340880306239L;

    protected long timeOfMostRecentAccess;
    protected String id;
    protected long duration;

    public AbstractHashMonitoredContext(String id, long duration) {
        this.id = id;
        this.duration = duration;
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
        return this.getRemainingTime() > 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getId() {
        return this.id;
    }

    protected void ping() {
        this.timeOfMostRecentAccess = System.currentTimeMillis();
    }

}
