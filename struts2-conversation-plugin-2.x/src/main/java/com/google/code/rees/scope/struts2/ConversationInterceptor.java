package com.google.code.rees.scope.struts2;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.StrutsStatics;

import com.google.code.rees.scope.ActionProvider;
import com.google.code.rees.scope.conversation.ConversationAdapter;
import com.google.code.rees.scope.conversation.ConversationArbitrator;
import com.google.code.rees.scope.conversation.ConversationConfigurationProvider;
import com.google.code.rees.scope.conversation.ConversationManager;
import com.google.code.rees.scope.conversation.context.ConversationContextManager;
import com.google.code.rees.scope.conversation.context.HttpConversationContextManagerFactory;
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

    protected ConversationArbitrator arbitrator;
    protected ConversationManager conversationManager;
    protected ConversationConfigurationProvider conversationConfigurationProvider;
    protected ActionProvider finder;
    protected String actionSuffix;
    protected HttpConversationContextManagerFactory conversationContextManagerFactory;

    @Inject(StrutsConversationConstants.CONVERSATION_CONTEXT_MANAGER_FACTORY)
    public void setHttpConversationContextManagerFactory(
            HttpConversationContextManagerFactory conversationContextManagerFactory) {
        this.conversationContextManagerFactory = conversationContextManagerFactory;
    }

    @Inject(StrutsConversationConstants.ACTION_FINDER_KEY)
    public void setActionClassFinder(ActionProvider finder) {
        this.finder = finder;
    }

    @Inject(ConventionConstants.ACTION_SUFFIX)
    public void setActionSuffix(String suffix) {
        this.actionSuffix = suffix;
    }

    @Inject(StrutsConversationConstants.CONVERSATION_ARBITRATOR_KEY)
    public void setArbitrator(ConversationArbitrator arbitrator) {
        this.arbitrator = arbitrator;
    }

    @Inject(StrutsConversationConstants.CONVERSATION_MANAGER_KEY)
    public void setConversationManager(ConversationManager manager) {
        this.conversationManager = manager;
    }

    @Inject(StrutsConversationConstants.CONVERSATION_CONFIG_PROVIDER_KEY)
    public void setConversationConfigurationProvider(
            ConversationConfigurationProvider conversationConfigurationProvider) {
        this.conversationConfigurationProvider = conversationConfigurationProvider;
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
        HttpServletRequest request = (HttpServletRequest) invocation
                .getInvocationContext().get(StrutsStatics.HTTP_REQUEST);
        ConversationContextManager contextManager = this.conversationContextManagerFactory
                .getManager(request);
        this.conversationManager
                .processConversations(new StrutsConversationAdapter(invocation,
                        contextManager));
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
        stackItem.put(StrutsConversationConstants.CONVERSATION_ID_MAP_STACK_KEY,
                ConversationAdapter.getAdapter().getViewContext());
        invocation.getStack().push(stackItem);
    }

}
