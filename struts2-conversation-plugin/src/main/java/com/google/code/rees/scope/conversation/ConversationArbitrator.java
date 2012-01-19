package com.google.code.rees.scope.conversation;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public interface ConversationArbitrator {
	public boolean isConversationField(Class<?> actionClass, Field field);
	public boolean isConversationAction(Class<?> actionClass, Method method);
	public boolean isBeginAction(Class<?> actionClass, Method method);
	public boolean isEndAction(Class<?> actionClass, Method method);
}
