package com.google.code.rees.scope.conversation.context;

import java.util.TimerTask;

import com.google.code.rees.scope.util.AbstractHashMonitoredContext;

public class DefaultConversationContext extends
        AbstractHashMonitoredContext<String, Object> implements
        ConversationContext {

    private static final long serialVersionUID = 2795735781863798576L;
    protected String conversationName;
    protected transient TimerTask timerTask;

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
    public void setTimerTask(TimerTask timerTask) {
        this.timerTask = timerTask;
    }

    @Override
    public TimerTask getTimerTask() {
        return this.timerTask;
    }

}
