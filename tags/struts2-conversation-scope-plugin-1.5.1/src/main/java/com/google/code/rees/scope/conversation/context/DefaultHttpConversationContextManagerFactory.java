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
 *  $Id: DefaultHttpConversationContextManagerFactory.java reesbyars $
 ******************************************************************************/
package com.google.code.rees.scope.conversation.context;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.code.rees.scope.conversation.ConversationConstants;
import com.google.code.rees.scope.util.monitor.BasicTimeoutMonitor;
import com.google.code.rees.scope.util.monitor.TimeoutMonitor;

/**
 * The default implementation of the
 * {@link HttpConversationContextManagerFactory}
 * 
 * @author rees.byars
 * 
 */
public class DefaultHttpConversationContextManagerFactory implements HttpConversationContextManagerFactory {

    private static final long serialVersionUID = 1500381458203865515L;

    private static Logger LOG = LoggerFactory.getLogger(DefaultHttpConversationContextManagerFactory.class);

    protected long defaultMaxIdleTime = ConversationConstants.DEFAULT_CONVERSATION_MAX_IDLE_TIME;
    protected long monitoringFrequency = TimeoutMonitor.DEFAULT_MONITOR_FREQUENCY;
    protected int maxInstances = ConversationConstants.DEFAULT_MAXIMUM_NUMBER_OF_A_GIVEN_CONVERSATION;
    protected ConversationContextFactory conversationContextFactory;
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void setDefaultMaxIdleTime(long timeout) {
        this.defaultMaxIdleTime = timeout;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setMonitoringFrequency(long monitoringFrequency) {
        this.monitoringFrequency = monitoringFrequency;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setMaxInstances(int maxInstances) {
        this.maxInstances = maxInstances;
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
    	Object contextManager = HttpConversationUtil.getContextManager(session);
        if (contextManager == null) {
        	contextManager = this.createContextManager(session);
        }
        return (ConversationContextManager) contextManager;
    }
    
    protected ConversationContextManager createContextManager(HttpSession session) {
    	if (LOG.isDebugEnabled()) {
    		LOG.debug("Creating new ConversationContextManager for session with ID:  " + session.getId());
    	}
    	TimeoutConversationContextManager contextManager = new TimeoutConversationContextManager();
    	contextManager.setDefaultMaxIdleTime(this.defaultMaxIdleTime);
    	contextManager.setMaxInstances(this.maxInstances);
    	contextManager.setContextFactory(this.conversationContextFactory);
        TimeoutMonitor<ConversationContext> timeoutMonitor = BasicTimeoutMonitor.spawnInstance(this.monitoringFrequency);
        contextManager.setTimeoutMonitor(timeoutMonitor);
        HttpConversationUtil.setContextManager(session, contextManager);
        return contextManager;
    }

}
