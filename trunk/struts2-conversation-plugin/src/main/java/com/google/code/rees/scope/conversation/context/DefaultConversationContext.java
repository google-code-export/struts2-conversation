package com.google.code.rees.scope.conversation.context;

import com.google.code.rees.scope.util.AbstractHashMonitoredContext;

public class DefaultConversationContext extends
        AbstractHashMonitoredContext<String, Object> implements
        ConversationContext {

    private static final long serialVersionUID = 2795735781863798576L;
    protected String conversationName;

    public DefaultConversationContext(String conversationName, String id,
            long duration) {
        super(id, duration);
        this.conversationName = conversationName;
    }

    @Override
    public String getConversationName() {
        return this.conversationName;
    }

    @Override
    public <T> T getBean(Class<T> beanClass) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T> T getBean(Class<T> beanClass, String beanName) {
        // TODO Auto-generated method stub
        return null;
    }

}
