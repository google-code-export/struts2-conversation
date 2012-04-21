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
 *  $Id: ScopeInterceptor.java reesbyars $
 ******************************************************************************/
package com.google.code.rees.scope.struts2;

import java.util.HashMap;
import java.util.Map;

import com.google.code.rees.scope.ActionProvider;
import com.google.code.rees.scope.DefaultScopeManager;
import com.google.code.rees.scope.ScopeAdapterFactory;
import com.google.code.rees.scope.ScopeManager;
import com.google.code.rees.scope.conversation.ConversationAdapter;
import com.google.code.rees.scope.conversation.configuration.ConversationArbitrator;
import com.google.code.rees.scope.conversation.configuration.ConversationConfigurationProvider;
import com.google.code.rees.scope.conversation.processing.ConversationManager;
import com.google.code.rees.scope.session.SessionConfigurationProvider;
import com.google.code.rees.scope.session.SessionManager;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.interceptor.Interceptor;
import com.opensymphony.xwork2.interceptor.MethodFilterInterceptor;
import com.opensymphony.xwork2.interceptor.PreResultListener;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;

/**
 * A Struts2 {@link Interceptor} that uses injected Conversations and Session
 * scope management components to process and manage conversation and
 * session-scoped
 * beans and lifecycles.
 * <p>
 * Also implements {@link PreResultListener} to perform post-processing and
 * place the {@link ConversationAdapter#getViewContext()} map onto the
 * {@link ValueStack} in order to make the conversation IDs available to the
 * view.
 * 
 * @author rees.byars
 */
public class ScopeInterceptor extends MethodFilterInterceptor implements
        PreResultListener {

    private static final long serialVersionUID = 3222190171260674636L;
    private static final Logger LOG = LoggerFactory
            .getLogger(ScopeInterceptor.class);

    protected ScopeManager manager = new DefaultScopeManager();
    protected ScopeAdapterFactory adapterFactory = new StrutsScopeAdapterFactory();
    protected ConversationArbitrator arbitrator;
    protected ConversationManager conversationManager;
    protected ConversationConfigurationProvider conversationConfigurationProvider;
    protected SessionManager sessionManager;
    protected SessionConfigurationProvider sessionConfigurationProvider;
    protected ActionProvider finder;
    protected String actionSuffix;

    @Inject(StrutsScopeConstants.ACTION_FINDER_KEY)
    public void setActionClassFinder(ActionProvider finder) {
        this.finder = finder;
    }

    @Inject(ConventionConstants.ACTION_SUFFIX)
    public void setActionSuffix(String suffix) {
        this.actionSuffix = suffix;
    }

    @Inject(StrutsScopeConstants.SCOPE_MANAGER_KEY)
    public void setScopeManager(ScopeManager manager) {
        this.manager = manager;
    }

    @Inject(StrutsScopeConstants.CONVERSATION_ARBITRATOR_KEY)
    public void setArbitrator(ConversationArbitrator arbitrator) {
        this.arbitrator = arbitrator;
    }

    @Inject(StrutsScopeConstants.CONVERSATION_MANAGER_KEY)
    public void setConversationManager(ConversationManager manager) {
        this.conversationManager = manager;
    }

    @Inject(StrutsScopeConstants.CONVERSATION_CONFIG_PROVIDER_KEY)
    public void setConversationConfigurationProvider(
            ConversationConfigurationProvider conversationConfigurationProvider) {
        this.conversationConfigurationProvider = conversationConfigurationProvider;
    }

    @Inject(StrutsScopeConstants.SESSION_MANAGER_KEY)
    public void setSessionManager(SessionManager manager) {
        this.sessionManager = manager;
    }

    @Inject(StrutsScopeConstants.SESSION_CONFIG_PROVIDER_KEY)
    public void setSessionConfigurationProvider(
            SessionConfigurationProvider sessionConfigurationProvider) {
        this.sessionConfigurationProvider = sessionConfigurationProvider;
    }

    @Inject(StrutsScopeConstants.SCOPE_ADAPTER_FACTORY_KEY)
    public void setAdapterFactory(ScopeAdapterFactory adapterFactory) {
        this.adapterFactory = adapterFactory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void destroy() {
        LOG.info("Destroying the ScopeInterceptor...");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init() {

        LOG.info("Initializing the ScopeInterceptor...");

        this.arbitrator.setActionSuffix(actionSuffix);
        this.conversationConfigurationProvider.setArbitrator(arbitrator);
        this.conversationConfigurationProvider.init(this.finder
                .getActionClasses());
        this.conversationManager
                .setConfigurationProvider(conversationConfigurationProvider);

        this.sessionConfigurationProvider.init(finder.getActionClasses());
        this.sessionManager
                .setConfigurationProvider(sessionConfigurationProvider);

        this.manager.setConversationManager(conversationManager);
        this.manager.setSessionManager(sessionManager);
        this.manager.setScopeAdapterFactory(adapterFactory);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String doIntercept(ActionInvocation invocation) throws Exception {
        this.manager.processScopes();
        invocation.addPreResultListener(this);
        return invocation.invoke();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void beforeResult(ActionInvocation invocation, String result) {
        ConversationAdapter.getAdapter().executePostProcessors();
        this.pushViewContextOntoStack(invocation);
    }

    protected void pushViewContextOntoStack(ActionInvocation invocation) {
        Map<String, Map<String, String>> stackItem = new HashMap<String, Map<String, String>>();
        stackItem.put(StrutsScopeConstants.CONVERSATION_ID_MAP_STACK_KEY,
                ConversationAdapter.getAdapter().getViewContext());
        invocation.getStack().push(stackItem);
    }

}
