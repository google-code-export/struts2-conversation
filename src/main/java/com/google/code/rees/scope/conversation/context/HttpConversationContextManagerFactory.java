package com.google.code.rees.scope.conversation.context;

import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;

public interface HttpConversationContextManagerFactory extends Serializable {

    public ConversationContextManager getManager(HttpServletRequest request);

}
