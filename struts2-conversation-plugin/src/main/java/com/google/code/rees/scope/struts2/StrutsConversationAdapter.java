package com.google.code.rees.scope.struts2;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.StrutsStatics;

import com.google.code.rees.scope.conversation.ConversationAdapter;
import com.google.code.rees.scope.conversation.ConversationContextFactory;
import com.google.code.rees.scope.conversation.MonitoredConversationContextFactory;
import com.google.code.rees.scope.util.RequestContextUtil;
import com.google.code.rees.scope.util.SessionContextUtil;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;

public class StrutsConversationAdapter extends ConversationAdapter {

    private static final long serialVersionUID = -907192380776385729L;

    protected final static ConversationContextFactory conversationContextFactory = new MonitoredConversationContextFactory();

    protected ActionInvocation invocation;
    protected ActionContext actionContext;
    protected HttpServletRequest request;
    protected Map<String, String> requestContext;

    public StrutsConversationAdapter(ActionInvocation invocation) {
        this.invocation = invocation;
        this.actionContext = invocation.getInvocationContext();
    }

    @Override
    public Object getAction() {
        return invocation.getAction();
    }

    @Override
    public String getActionId() {
        return invocation.getProxy().getMethod();
    }

    @Override
    public Map<String, Object> getSessionContext() {
        return SessionContextUtil
                .getSessionContext(((HttpServletRequest) this.actionContext
                        .get(StrutsStatics.HTTP_REQUEST)));
    }

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

    @Override
    public Map<String, Object> createConversationContext(String conversationId,
            Map<String, Object> sessionContext) {
        return conversationContextFactory.createConversationContext(
                conversationId, sessionContext);
    }

}