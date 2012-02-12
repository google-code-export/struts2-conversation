package com.google.code.rees.scope;

import java.io.Serializable;

import com.google.code.rees.scope.conversation.ConversationAdapter;
import com.google.code.rees.scope.session.SessionAdapter;

/**
 * A factory used by the {@link ScopeManager} for creating adapters
 * for processing scopes.
 * 
 * @author rees.byars
 */
public interface ScopeAdapterFactory extends Serializable {

    /**
     * Creates a {@link SessionAdapter}
     * 
     * @return
     */
    public SessionAdapter createSessionAdapter();

    /**
     * Creates a {@link ConversationAdapter}
     * 
     * @return
     */
    public ConversationAdapter createConversationAdapter();

}
