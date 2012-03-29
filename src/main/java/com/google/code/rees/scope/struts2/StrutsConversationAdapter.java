package com.google.code.rees.scope.struts2;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.StrutsStatics;

import com.google.code.rees.scope.conversation.ConversationAdapter;
import com.google.code.rees.scope.conversation.context.ConversationContext;
import com.google.code.rees.scope.conversation.context.ConversationContextManager;
import com.google.code.rees.scope.util.RequestContextUtil;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;

/**
 * Struts2 implementation of the {@link ConversationAdapter}.
 * 
 * @author rees.byars
 */
public class StrutsConversationAdapter extends ConversationAdapter {

    private static final long serialVersionUID = -907192380776385729L;

    protected ConversationContextManager conversationContextManager;
    protected ActionInvocation invocation;
    protected ActionContext actionContext;
    protected HttpServletRequest request;
    protected Map<String, String> requestContext;

    public StrutsConversationAdapter(ActionInvocation invocation,
            ConversationContextManager conversationContextManager) {
        this.invocation = invocation;
        this.actionContext = invocation.getInvocationContext();
        this.conversationContextManager = conversationContextManager;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getAction() {
        return invocation.getAction();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getActionId() {
        return invocation.getProxy().getMethod();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, String> getRequestContext() {
        ActionContext actionContext = ActionContext.getContext();
        if (actionContext != null) {
            HttpServletRequest currentRequest = ((HttpServletRequest) ActionContext
                    .getContext().get(StrutsStatics.HTTP_REQUEST));
            if (!currentRequest.equals(this.request)) {
                this.request = currentRequest;
                requestContext = RequestContextUtil
                        .getRequestContext(currentRequest);
            }
        } else {
            // TODO: this is a hack to get unit tests to work with spring
            // autowiring - revisit to determine more optimal solution
            requestContext = new HashMap<String, String>();
        }
        return requestContext;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ConversationContext getConversationContext(String conversationName,
            String conversationId) {
        return this.conversationContextManager.getContext(conversationName,
                conversationId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ConversationContext endConversation(String conversationName,
            String conversationId) {
        return this.conversationContextManager.remove(conversationName,
                conversationId);
    }

}