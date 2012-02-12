package com.google.code.rees.scope;

import java.io.Serializable;

import com.google.code.rees.scope.conversation.ConversationManager;
import com.google.code.rees.scope.session.SessionManager;

public interface ScopeManager extends Serializable {

    public void setConversationManager(ConversationManager manager);

    public void setSessionManager(SessionManager manager);

    public void setScopeAdapterFactory(ScopeAdapterFactory adapterFactory);

    public void processScopes();

}
