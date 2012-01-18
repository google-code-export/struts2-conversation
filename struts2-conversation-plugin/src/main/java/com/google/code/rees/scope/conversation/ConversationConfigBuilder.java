package com.google.code.rees.scope.conversation;

import java.util.Collection;
import java.util.Map;

public interface ConversationConfigBuilder {

	public Map<Class<?>, Collection<ConversationConfig>> getConversationConfigs();
	public Collection<ConversationConfig> addClassConfig(Class<?> clazz);

}
