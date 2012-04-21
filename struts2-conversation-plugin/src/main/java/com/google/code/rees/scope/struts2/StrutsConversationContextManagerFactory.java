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
 *  $Id: StrutsConversationContextManagerFactory.java reesbyars $
 ******************************************************************************/
package com.google.code.rees.scope.struts2;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.code.rees.scope.conversation.context.ConversationContext;
import com.google.code.rees.scope.conversation.context.ConversationContextFactory;
import com.google.code.rees.scope.conversation.context.ConversationContextManager;
import com.google.code.rees.scope.conversation.context.HttpConversationContextManagerFactory;
import com.google.code.rees.scope.conversation.context.HttpConversationUtil;
import com.google.code.rees.scope.conversation.context.TimeoutConversationContextManager;
import com.google.code.rees.scope.util.monitor.BasicTimeoutMonitor;
import com.google.code.rees.scope.util.monitor.TimeoutMonitor;
import com.opensymphony.xwork2.inject.Inject;

/**
 * 
 * @author rees.byars
 *
 */
public class StrutsConversationContextManagerFactory implements HttpConversationContextManagerFactory {

    private static final long serialVersionUID = 2461287910903625512L;
    
    private static Logger LOG = LoggerFactory.getLogger(StrutsConversationContextManagerFactory.class);

    protected Long monitoringFrequency;
    protected Long timeout;
    protected Integer maxInstances;
    protected ConversationContextFactory conversationContextFactory;

    @Inject(StrutsScopeConstants.CONVERSATION_MONITORING_FREQUENCY)
    public void setMonitoringFrequency(String monitoringFrequency) {
        this.monitoringFrequency = Long.parseLong(monitoringFrequency);
    }

    @Inject(StrutsScopeConstants.CONVERSATION_IDLE_TIMEOUT)
    public void setTimeout(String timeout) {
        this.timeout = Long.parseLong(timeout);
    }

    @Inject(StrutsScopeConstants.CONVERSATION_MAX_INSTANCES)
    public void setMaxInstances(String maxInstances) {
        this.maxInstances = Integer.parseInt(maxInstances);
    }

    @Inject(StrutsScopeConstants.CONVERSATION_CONTEXT_FACTORY)
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
    	contextManager.setConversationDuration(this.timeout);
    	contextManager.setMaxInstances(this.maxInstances);
    	contextManager.setContextFactory(this.conversationContextFactory);
        TimeoutMonitor<ConversationContext> timeoutMonitor = BasicTimeoutMonitor.spawnInstance(this.monitoringFrequency);
        contextManager.setTimeoutMonitor(timeoutMonitor);
        HttpConversationUtil.setContextManager(session, contextManager);
        return contextManager;
    }

}
