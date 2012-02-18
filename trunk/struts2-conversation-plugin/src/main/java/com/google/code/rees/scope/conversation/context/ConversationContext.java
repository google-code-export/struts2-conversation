package com.google.code.rees.scope.conversation.context;

import com.google.code.rees.scope.util.MonitoredContext;

public interface ConversationContext extends MonitoredContext<String, Object> {

    public String getConversationName();

    public <T> T getBean(Class<T> beanClass);

    public <T> T getBean(Class<T> beanClass, String beanName);

}
