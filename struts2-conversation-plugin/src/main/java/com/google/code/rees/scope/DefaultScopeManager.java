package com.google.code.rees.scope;

import com.google.code.rees.scope.conversation.ConversationManager;
import com.google.code.rees.scope.session.SessionManager;

/**
 * 
 * @author rees.byars
 * 
 */
public class DefaultScopeManager implements ScopeManager {

    private static final long serialVersionUID = -7042031513311747101L;

    protected SessionManager sessionManager;
    protected ConversationManager conversationManager;
    protected ScopeAdapterFactory adapterFactory;

    @Override
    public void setConversationManager(ConversationManager manager) {
        this.conversationManager = manager;
    }

    @Override
    public void setSessionManager(SessionManager manager) {
        this.sessionManager = manager;
    }

    @Override
    public void setScopeAdapterFactory(ScopeAdapterFactory adapterFactory) {
        this.adapterFactory = adapterFactory;
    }

    @Override
    public void processScopes() {
        this.sessionManager.processSessionFields(this.adapterFactory
                .createSessionAdapter());
        this.conversationManager.processConversations(this.adapterFactory
                .createConversationAdapter());
    }

}
