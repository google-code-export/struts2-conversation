package com.google.code.struts2.scope.spring;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;

import com.google.code.struts2.scope.conversation.ConversationUtil;

public class ConversationScope implements Scope {

	@Override
	public Object get(String name, ObjectFactory<?> objectFactory) {
		
		Object object = SpringConversationUtil.getConversationField(name);
		if (object == null) {
			object = objectFactory.getObject();
			ConversationUtil.setConversationField(name, object);
		}
		return object;
	}

	@Override
	public String getConversationId() {
		return ConversationUtil.getConversationIds()[0];
	}

	@Override
	public void registerDestructionCallback(String name, Runnable destructionCallback) {
	}

	@Override
	public Object remove(String name) {
		return null;
	}

	@Override
	public Object resolveContextualObject(String name) {
		return null;
	}

	
}
