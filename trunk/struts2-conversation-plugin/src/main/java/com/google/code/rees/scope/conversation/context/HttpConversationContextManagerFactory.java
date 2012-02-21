package com.google.code.rees.scope.conversation.context;

import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;

/**
 * Used for creating {@link ConversationContextManager
 * ConversationContextManagers} that are tied to the given request (or more
 * likely, that request's session)
 * 
 * @author rees.byars
 * 
 */
public interface HttpConversationContextManagerFactory extends Serializable {

    /**
     * Return the {@link ConversationContextManager} for this request
     * 
     * @param request
     * @return
     */
    public ConversationContextManager getManager(HttpServletRequest request);

}
