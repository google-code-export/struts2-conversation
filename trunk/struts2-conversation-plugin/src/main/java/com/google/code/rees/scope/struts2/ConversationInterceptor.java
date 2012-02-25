package com.google.code.rees.scope.struts2;

import java.util.HashMap;
import java.util.Map;

import com.google.code.rees.scope.ActionProvider;
import com.google.code.rees.scope.ScopeAdapterFactory;
import com.google.code.rees.scope.conversation.ConversationAdapter;
import com.google.code.rees.scope.conversation.ConversationArbitrator;
import com.google.code.rees.scope.conversation.ConversationConfigurationProvider;
import com.google.code.rees.scope.conversation.ConversationManager;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.interceptor.MethodFilterInterceptor;
import com.opensymphony.xwork2.interceptor.PreResultListener;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;

public class ConversationInterceptor extends MethodFilterInterceptor implements
        PreResultListener {

    private static final long serialVersionUID = -72776817859403642L;
    private static final Logger LOG = LoggerFactory
            .getLogger(ConversationInterceptor.class);

    protected ScopeAdapterFactory adapterFactory = new StrutsScopeAdapterFactory();
    protected ConversationArbitrator arbitrator;
    protected ConversationManager conversationManager;
    protected ConversationConfigurationProvider conversationConfigurationProvider;
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

    @Inject(StrutsScopeConstants.CONVERSATION_ARBITRATOR_KEY)
    public void setArbitrator(ConversationArbitrator arbitrator) {
        this.arbitrator = arbitrator;
    }

    @Inject(StrutsScopeConstants.SIMPLE_CONVERSATION_MANAGER_KEY)
    public void setConversationManager(ConversationManager manager) {
        this.conversationManager = manager;
    }

    @Inject(StrutsScopeConstants.CONVERSATION_CONFIG_PROVIDER_KEY)
    public void setConversationConfigurationProvider(
            ConversationConfigurationProvider conversationConfigurationProvider) {
        this.conversationConfigurationProvider = conversationConfigurationProvider;
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
        LOG.info("Destroying the ConversationInterceptor...");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init() {

        LOG.info("Initializing the ConversationInterceptor...");

        this.arbitrator.setActionSuffix(actionSuffix);
        this.conversationConfigurationProvider.setArbitrator(arbitrator);
        this.conversationConfigurationProvider.init(this.finder
                .getActionClasses());
        this.conversationManager
                .setConfigurationProvider(conversationConfigurationProvider);

    }

    @Override
    protected String doIntercept(ActionInvocation invocation) throws Exception {
        this.conversationManager.processConversations(this.adapterFactory
                .createConversationAdapter());
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
