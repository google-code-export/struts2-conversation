package com.google.code.rees.scope;

import java.io.Serializable;

import com.google.code.rees.scope.conversation.ConversationManager;
import com.google.code.rees.scope.session.SessionManager;

/**
 * Uses a {@link ConversationManager} and {@link SessionManager} to provide
 * external frameworks with central manager for
 * processing both the conversation and session scopes.
 * 
 * @author rees.byars
 */
public interface ScopeManager extends Serializable {

    /**
     * Sets the {@link ConversationManager} that this manager will use
     * 
     * @param manager
     */
    public void setConversationManager(ConversationManager manager);

    /**
     * Sets the {@link SessionManager} that this manager will use
     * 
     * @param manager
     */
    public void setSessionManager(SessionManager manager);

    /**
     * Sets the {@link ScopeAdapterFactory} that this manager will use
     * for generating scope adapters.
     * 
     * @param adapterFactory
     */
    public void setScopeAdapterFactory(ScopeAdapterFactory adapterFactory);

    /**
     * Performs the scope processing for the conversation and session scopes
     */
    public void processScopes();

}
