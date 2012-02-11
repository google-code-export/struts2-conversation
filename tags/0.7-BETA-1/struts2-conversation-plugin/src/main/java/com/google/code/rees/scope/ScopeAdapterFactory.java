package com.google.code.rees.scope;

import java.io.Serializable;

import com.google.code.rees.scope.conversation.ConversationAdapter;
import com.google.code.rees.scope.session.SessionAdapter;

public interface ScopeAdapterFactory extends Serializable {
	public SessionAdapter createSessionAdapter();
	public ConversationAdapter createConversationAdapter();
}
