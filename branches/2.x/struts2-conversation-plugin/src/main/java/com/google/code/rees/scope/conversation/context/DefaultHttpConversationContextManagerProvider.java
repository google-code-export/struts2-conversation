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
 *  $Id: DefaultHttpConversationContextManagerProvider.java reesbyars $
 ******************************************************************************/
package com.google.code.rees.scope.conversation.context;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.code.rees.scope.conversation.ConversationConstants;
import com.google.code.rees.scope.util.monitor.ScheduledExecutorTimeoutMonitor;
import com.google.code.rees.scope.util.monitor.SchedulerProvider;
import com.google.code.rees.scope.util.monitor.TimeoutMonitor;

/**
 * The default implementation of the
 * {@link HttpConversationContextManagerProvider}
 * 
 * @author rees.byars
 * 
 */
public class DefaultHttpConversationContextManagerProvider implements HttpConversationContextManagerProvider, SchedulerProvider {

    private static final long serialVersionUID = 1500381458203865515L;

    private static Logger LOG = LoggerFactory.getLogger(DefaultHttpConversationContextManagerProvider.class);

    protected long monitoringFrequency = TimeoutMonitor.DEFAULT_MONITOR_FREQUENCY;
    protected int monitoringThreadPoolSize = ConversationConstants.DEFAULT_MONITORING_THREAD_POOL_SIZE;
    protected ConversationContextFactory conversationContextFactory;
    protected transient ScheduledExecutorService scheduler;
    protected Lock schedulerGuard = new ReentrantLock();
    
    @PostConstruct
    public void init() {
    	this.scheduler = Executors.newScheduledThreadPool(this.monitoringThreadPoolSize, new ThreadFactory() {
    		
    		AtomicInteger id = new AtomicInteger(0);

			@Override
			public Thread newThread(Runnable runnable) {
				Thread thread = new Thread(runnable);
				thread.setDaemon(true);
				thread.setName("ConversationTimeoutMonitoringThread-" + id.getAndIncrement());
				return thread;
			}
    		
    	});
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void setMonitoringThreadPoolSize(int monitoringThreadPoolSize) {
    	LOG.info("Setting conversation monitoring thread-pool size:  " + monitoringThreadPoolSize + " threads.");
    	this.monitoringThreadPoolSize = monitoringThreadPoolSize;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setMonitoringFrequency(long monitoringFrequency) {
    	double monitoringFrequencyMinutes = monitoringFrequency / (1000.0 * 60);
    	LOG.info("Setting conversation timeout monitoring frequency:  " + monitoringFrequency + " milliseconds.");
    	LOG.info("Converted monitoring frequency:  " + String.format("%.2f", monitoringFrequencyMinutes) + " minutes.");
        this.monitoringFrequency = monitoringFrequency;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setConversationContextFactory(ConversationContextFactory conversationContextFactory) {
        this.conversationContextFactory = conversationContextFactory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ConversationContextManager getManager(HttpServletRequest request) { 
    	HttpSession session = request.getSession();
    	ConversationContextManager contextManager = HttpConversationUtil.getContextManager(session);
        if (contextManager == null) {
        	contextManager = this.createContextManager(session);
        } else {
        	ScheduledExecutorTimeoutMonitor<ConversationContext> monitor = (ScheduledExecutorTimeoutMonitor<ConversationContext>) HttpConversationUtil.getTimeoutMonitor(session);
        	monitor.setSchedulerProvider(this);
        }
        return contextManager;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
	public ScheduledExecutorService getScheduler() {
		if (scheduler != null) {
			return scheduler;
		}
		schedulerGuard.lock();
		try {
			if (scheduler !=null) {
				return scheduler;
			}
			this.init();
			return scheduler;
		} finally {
			schedulerGuard.unlock();
		}
		
	}
    
    protected ConversationContextManager createContextManager(HttpSession session) {
    	if (LOG.isDebugEnabled()) {
    		LOG.debug("Creating new ConversationContextManager for session with ID:  " + session.getId());
    	}
    	TimeoutConversationContextManager contextManager = new TimeoutConversationContextManager();
    	contextManager.setContextFactory(this.conversationContextFactory);
        TimeoutMonitor<ConversationContext> timeoutMonitor = ScheduledExecutorTimeoutMonitor.spawnInstance(this, this.monitoringFrequency);
        contextManager.setTimeoutMonitor(timeoutMonitor);
        HttpConversationUtil.setContextManager(session, contextManager);
        HttpConversationUtil.setTimeoutMonitor(session, timeoutMonitor);
        return contextManager;
    }

}
