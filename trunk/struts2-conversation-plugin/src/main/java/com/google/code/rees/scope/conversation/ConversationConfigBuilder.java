package com.google.code.rees.scope.conversation;

import java.util.Collection;
import java.util.Map;

public interface ConversationConfigBuilder {

	public Map<Class<?>, Collection<ConversationConfiguration>> getConversationConfigs();
	public Collection<ConversationConfiguration> addClassConfig(Class<?> clazz);

}
