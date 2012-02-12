package com.google.code.rees.scope.spring;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.method.HandlerMethod;

import com.google.code.rees.scope.conversation.ConversationAdapter;
import com.google.code.rees.scope.conversation.ConversationContextFactory;
import com.google.code.rees.scope.conversation.MonitoredConversationContextFactory;
import com.google.code.rees.scope.util.RequestContextUtil;
import com.google.code.rees.scope.util.SessionContextUtil;

public class SpringConversationAdapter extends ConversationAdapter {

    private static final long serialVersionUID = 5664922664767226366L;

    protected final static ConversationContextFactory conversationContextFactory = new MonitoredConversationContextFactory();

    protected Map<String, Object> sessionContext;
    protected Map<String, String> requestContext;
    protected Object action;
    protected String actionId;
    protected HttpServletRequest request;

    public SpringConversationAdapter(HttpServletRequest request,
            HandlerMethod handler) {
        this.sessionContext = SessionContextUtil.getSessionContext(request);
        this.requestContext = RequestContextUtil.getRequestContext(request);
        this.action = handler.getBean();
        this.actionId = handler.getMethod().getName();
    }

    @Override
    public Map<String, Object> createConversationContext(String conversationId,
            Map<String, Object> sessionContext) {
        return conversationContextFactory.createConversationContext(
                conversationId, sessionContext);
    }

    @Override
    public Object getAction() {
        return this.action;
    }

    @Override
    public String getActionId() {
        return this.actionId;
    }

    @Override
    public Map<String, Object> getSessionContext() {
        return this.sessionContext;
    }

    @Override
    public Map<String, String> getRequestContext() {

        HttpServletRequest currentRequest = ((ServletRequestAttributes) RequestContextHolder
                .currentRequestAttributes()).getRequest();

        if (!currentRequest.equals(this.request)) {
            this.request = currentRequest;
            requestContext = RequestContextUtil
                    .getRequestContext(currentRequest);
        }

        return this.requestContext;
    }

}
