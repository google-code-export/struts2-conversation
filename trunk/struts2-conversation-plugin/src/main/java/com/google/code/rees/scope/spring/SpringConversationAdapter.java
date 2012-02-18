package com.google.code.rees.scope.spring;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.method.HandlerMethod;

import com.google.code.rees.scope.conversation.ConversationAdapter;
import com.google.code.rees.scope.conversation.ConversationConstants;
import com.google.code.rees.scope.conversation.context.ConversationContextManager;
import com.google.code.rees.scope.conversation.context.DefaultConversationContextManager;
import com.google.code.rees.scope.util.RequestContextUtil;
import com.google.code.rees.scope.util.SessionContextUtil;

/**
 * An implementation of the {@link ConversationAdapter} for use with Spring MVC.
 * 
 * @author rees.byars
 * 
 */
public class SpringConversationAdapter extends ConversationAdapter {

    private static final long serialVersionUID = 5664922664767226366L;

    protected ConversationContextManager conversationContextManager;
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
        this.conversationContextManager = getManager(request);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getAction() {
        return this.action;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getActionId() {
        return this.actionId;
    }

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Object> getConversationContext(String conversationName,
            String conversationId) {
        return this.conversationContextManager.getContext(conversationName,
                conversationId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Object> endConversation(String conversationName,
            String conversationId) {
        return this.conversationContextManager.remove(conversationName,
                conversationId);
    }

    protected ConversationContextManager getManager(HttpServletRequest request) {
        HttpSession session = request.getSession();
        Object ctxMgr = session
                .getAttribute(ConversationConstants.CONVERSATION_CONTEXT_MANAGER_KEY);
        if (ctxMgr == null) {
            ctxMgr = new DefaultConversationContextManager();
            session.setAttribute(
                    ConversationConstants.CONVERSATION_CONTEXT_MANAGER_KEY,
                    ctxMgr);
        }
        return (ConversationContextManager) ctxMgr;
    }

}
