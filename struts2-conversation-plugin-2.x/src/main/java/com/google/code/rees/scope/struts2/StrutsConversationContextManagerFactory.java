package com.google.code.rees.scope.struts2;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.google.code.rees.scope.conversation.ConversationConstants;
import com.google.code.rees.scope.conversation.context.ConversationContextFactory;
import com.google.code.rees.scope.conversation.context.ConversationContextManager;
import com.google.code.rees.scope.conversation.context.DefaultConversationContextManager;
import com.google.code.rees.scope.conversation.context.HttpConversationContextManagerFactory;
import com.opensymphony.xwork2.inject.Inject;

public class StrutsConversationContextManagerFactory implements
        HttpConversationContextManagerFactory {

    private static final long serialVersionUID = 2461287910903625512L;

    protected Long monitoringFrequency;
    protected Long timeout;
    protected Integer maxInstances;
    protected ConversationContextFactory conversationContextFactory;

    @Inject(StrutsConversationConstants.CONVERSATION_MONITORING_FREQUENCY)
    public void setMonitoringFrequency(String monitoringFrequency) {
        this.monitoringFrequency = Long.parseLong(monitoringFrequency);
    }

    @Inject(StrutsConversationConstants.CONVERSATION_IDLE_TIMEOUT)
    public void setTimeout(String timeout) {
        this.timeout = Long.parseLong(timeout);
        if (this.conversationContextFactory != null) {
            this.conversationContextFactory
                    .setConversationDuration(this.timeout);
        }
    }

    @Inject(StrutsConversationConstants.CONVERSATION_MAX_INSTANCES)
    public void setMaxInstances(String maxInstances) {
        this.maxInstances = Integer.parseInt(maxInstances);
    }

    @Inject(StrutsConversationConstants.CONVERSATION_CONTEXT_FACTORY)
    public void setConversationContextFactory(
            ConversationContextFactory conversationContextFactory) {
        this.conversationContextFactory = conversationContextFactory;
        if (this.timeout != null) {
            this.conversationContextFactory
                    .setConversationDuration(this.timeout);
        }
    }

    @Override
    public ConversationContextManager getManager(HttpServletRequest request) {
        HttpSession session = request.getSession();
        Object ctxMgr = null;
        ctxMgr = session
                .getAttribute(ConversationConstants.CONVERSATION_CONTEXT_MANAGER_KEY);
        if (ctxMgr == null) {
            ConversationContextManager newCtxMgr = new DefaultConversationContextManager();
            newCtxMgr.setContextFactory(this.conversationContextFactory);
            newCtxMgr.setMaxInstances(this.maxInstances);
            newCtxMgr.setMonitoringFrequency(this.monitoringFrequency);
            ctxMgr = newCtxMgr;
            session.setAttribute(
                    ConversationConstants.CONVERSATION_CONTEXT_MANAGER_KEY,
                    ctxMgr);
        }
        return (ConversationContextManager) ctxMgr;
    }

}
