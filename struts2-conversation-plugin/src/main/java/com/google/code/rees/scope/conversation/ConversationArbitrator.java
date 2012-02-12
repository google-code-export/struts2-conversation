package com.google.code.rees.scope.conversation;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;

public interface ConversationArbitrator extends Serializable {
    public void setActionSuffix(String suffix);

    public Collection<Field> getCandidateConversationFields(Class<?> clazz);

    public Collection<Method> getCandidateConversationMethods(Class<?> clazz);

    public Collection<String> getConversations(Class<?> clazz, Field field);

    public Collection<String> getConversations(Class<?> clazz, Method method);

    public String getName(Field field);

    public String getName(Method method);

    public Collection<String> getBeginConversations(Class<?> clazz,
            Method method);

    public Collection<String> getEndConversations(Class<?> clazz, Method method);
}
