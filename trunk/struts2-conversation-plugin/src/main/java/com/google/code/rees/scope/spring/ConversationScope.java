package com.google.code.rees.scope.spring;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;

import com.google.code.rees.scope.conversation.ConversationUtil;

/**
 * This custom scope must be configured in a Spring XML context.
 * It allows for conversation-scoped beans to be injected by the Spring IoC.
 * 
 * @author rees.byars
 */
public class ConversationScope implements Scope {

    @Override
    public Object get(String name, ObjectFactory<?> objectFactory) {
        Object object = ConversationUtil.getConversationField(name);
        if (object == null) {
            object = objectFactory.getObject();
            ConversationUtil.setConversationField(name, object);
        }
        return object;
    }

    @Override
    public String getConversationId() {
        return null;
    }

    @Override
    public void registerDestructionCallback(String name,
            Runnable destructionCallback) {
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
