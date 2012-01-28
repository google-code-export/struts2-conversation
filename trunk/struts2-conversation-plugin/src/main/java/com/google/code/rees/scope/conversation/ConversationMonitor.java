package com.google.code.rees.scope.conversation;

import java.io.Serializable;
import java.util.Map;

public interface ConversationMonitor extends Serializable {
	public void init(long conversationDuration);
	public void registerConversation(Map<String, Object> sessionContext, String conversationId);
	public void resetTimer(String conersationId);
	public void unregisterConversation(String conversationId);
}
