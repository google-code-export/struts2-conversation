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
 *  $Id: ConversationInterceptor.java reesbyars $
 ******************************************************************************/
package com.google.code.rees.scope.struts2;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.StrutsStatics;

import com.google.code.rees.scope.ActionProvider;
import com.google.code.rees.scope.conversation.ConversationAdapter;
import com.google.code.rees.scope.conversation.ConversationException;
import com.google.code.rees.scope.conversation.configuration.ConversationArbitrator;
import com.google.code.rees.scope.conversation.configuration.ConversationConfigurationProvider;
import com.google.code.rees.scope.conversation.context.ConversationContextFactory;
import com.google.code.rees.scope.conversation.context.ConversationContextManager;
import com.google.code.rees.scope.conversation.context.HttpConversationContextManagerFactory;
import com.google.code.rees.scope.conversation.processing.ConversationManager;
import com.google.code.rees.scope.conversation.processing.InjectionConversationManager;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.interceptor.Interceptor;
import com.opensymphony.xwork2.interceptor.PreResultListener;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;

/**
 * 
 * This interceptor uses an {@link InjectionConversationManager} to process conversation states and scopes.
 * 
 * @author rees.byars
 *
 */
public class ConversationInterceptor implements Interceptor, PreResultListener {

    private static final long serialVersionUID = -72776817859403642L;
    private static final Logger LOG = LoggerFactory.getLogger(ConversationInterceptor.class);
    
    /**
     * The result returned when a user submits an ID for a conversation that has already expired or ended.
     */
    public static final String CONVERSATION_EXCEPTION_RESULT = "conversation.exception";

    protected ActionProvider finder;
    protected String actionSuffix;
	protected long maxIdleTime;
    protected ConversationArbitrator arbitrator;
    protected ConversationConfigurationProvider conversationConfigurationProvider;
    protected ConversationManager conversationManager;
    protected HttpConversationContextManagerFactory conversationContextManagerFactory;
    protected int monitoringThreadPoolSize;
    protected long monitoringFrequency;
    protected int maxInstances;
    protected ConversationContextFactory conversationContextFactory;

    @Inject(StrutsScopeConstants.ACTION_FINDER_KEY)
    public void setActionClassFinder(ActionProvider finder) {
        this.finder = finder;
    }

    @Inject(ConventionConstants.ACTION_SUFFIX)
    public void setActionSuffix(String suffix) {
        this.actionSuffix = suffix;
    }
    
    @Inject(StrutsScopeConstants.CONVERSATION_IDLE_TIMEOUT)
    public void setDefaultMaxIdleTime(String defaultMaxIdleTimeString) {
        this.maxIdleTime = Long.parseLong(defaultMaxIdleTimeString);
    }

    @Inject(StrutsScopeConstants.CONVERSATION_ARBITRATOR_KEY)
    public void setArbitrator(ConversationArbitrator arbitrator) {
        this.arbitrator = arbitrator;
    }
    
    @Inject(StrutsScopeConstants.CONVERSATION_CONFIG_PROVIDER_KEY)
    public void setConversationConfigurationProvider(ConversationConfigurationProvider conversationConfigurationProvider) {
        this.conversationConfigurationProvider = conversationConfigurationProvider;
    }

    @Inject(StrutsScopeConstants.CONVERSATION_MANAGER_KEY)
    public void setConversationManager(ConversationManager manager) {
        this.conversationManager = manager;
    }

    @Inject(StrutsScopeConstants.CONVERSATION_CONTEXT_MANAGER_FACTORY)
    public void setHttpConversationContextManagerFactory(HttpConversationContextManagerFactory conversationContextManagerFactory) {
        this.conversationContextManagerFactory = conversationContextManagerFactory;
    }
    
    @Inject(StrutsScopeConstants.CONVERSATION_MONITORING_THREAD_POOL_SIZE)
    public void setMonitoringThreadPoolSize(String monitoringThreadPoolSizeString) {
    	this.monitoringThreadPoolSize = Integer.parseInt(monitoringThreadPoolSizeString);
    }

    @Inject(StrutsScopeConstants.CONVERSATION_MONITORING_FREQUENCY)
    public void setMonitoringFrequency(String monitoringFrequencyString) {
    	this.monitoringFrequency = Long.parseLong(monitoringFrequencyString);
    }

    @Inject(StrutsScopeConstants.CONVERSATION_MAX_INSTANCES)
    public void setMaxInstances(String maxInstancesString) {
    	this.maxInstances = Integer.parseInt(maxInstancesString);
    }

    @Inject(StrutsScopeConstants.CONVERSATION_CONTEXT_FACTORY)
    public void setConversationContextFactory(ConversationContextFactory conversationContextFactory) {
        this.conversationContextFactory = conversationContextFactory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void destroy() {
        LOG.info("Destroying the ConversationInterceptor...");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init() {

        LOG.info("Initializing the Conversation Interceptor...");

        this.arbitrator.setActionSuffix(this.actionSuffix);
        this.conversationConfigurationProvider.setArbitrator(this.arbitrator);
        this.conversationConfigurationProvider.setDefaultMaxIdleTime(this.maxIdleTime);
        this.conversationConfigurationProvider.init(this.finder.getActionClasses());
        this.conversationManager.setConfigurationProvider(this.conversationConfigurationProvider);
        
        this.conversationContextManagerFactory.setConversationContextFactory(this.conversationContextFactory);
        this.conversationContextManagerFactory.setMaxInstances(this.maxInstances);
        this.conversationContextManagerFactory.setMonitoringFrequency(this.monitoringFrequency);
        this.conversationContextManagerFactory.setMonitoringThreadPoolSize(this.monitoringThreadPoolSize);
        this.conversationContextManagerFactory.init();
        
        LOG.info("...Conversation Interceptor successfully initialized.");

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
    	HttpServletRequest request = (HttpServletRequest) invocation.getInvocationContext().get(StrutsStatics.HTTP_REQUEST);
    	ConversationContextManager contextManager = this.conversationContextManagerFactory.getManager(request);
    	try {
    		this.conversationManager.processConversations(new StrutsConversationAdapter(invocation, contextManager));
    	} catch (ConversationException e) {
    		return CONVERSATION_EXCEPTION_RESULT;
    	}
        invocation.addPreResultListener(this);
        return invocation.invoke();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void beforeResult(ActionInvocation invocation, String result) {
        ConversationAdapter.getAdapter().executePostProcessors();
        Map<String, Map<String, String>> stackItem = new HashMap<String, Map<String, String>>();
        stackItem.put(StrutsScopeConstants.CONVERSATION_ID_MAP_STACK_KEY, ConversationAdapter.getAdapter().getViewContext());
        invocation.getStack().push(stackItem);
    }

}
