package com.google.code.struts2.scope.conversation;

import java.util.Map;

public interface ConversationConfigBuilder {

	public Map<String, ConversationConfig> getConversationConfigs();
	public Map<String, ConversationConfig> addClassConfig(Class<?> clazz);

}
