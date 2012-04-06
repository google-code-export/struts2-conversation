package com.google.code.rees.scope.conversation.context;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.google.code.rees.scope.conversation.ConversationConstants;

public class DefaultHttpConversationContextManagerFactory implements
        HttpConversationContextManagerFactory {

    private static final long serialVersionUID = 1500381458203865515L;

    @Override
    public ConversationContextManager getManager(HttpServletRequest request) {
        HttpSession session = request.getSession();
        Object ctxMgr = null;
        ctxMgr = session
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