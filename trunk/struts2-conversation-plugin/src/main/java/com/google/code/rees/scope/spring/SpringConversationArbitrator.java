package com.google.code.rees.scope.spring;

import java.lang.reflect.Method;

import org.springframework.web.bind.annotation.RequestMapping;

import com.google.code.rees.scope.conversation.DefaultConversationArbitrator;

public class SpringConversationArbitrator extends DefaultConversationArbitrator {

	private static final long serialVersionUID = -2131295964932528989L;

	@Override
	protected boolean isAction(Method method) {
		return method.isAnnotationPresent(RequestMapping.class);
	}
}
