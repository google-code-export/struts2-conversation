package com.google.code.rees.scope.conversation.context;

import java.util.TimerTask;

import com.google.code.rees.scope.util.AbstractHashMonitoredContext;

/**
 * The default implementation of the {@link ConversationContext}
 * 
 * @author rees.byars
 * 
 */
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

    /**
     * {@inheritDoc}
     */
    @Override
    public String getConversationName() {
        return this.conversationName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setTimerTask(TimerTask timerTask) {
        this.timerTask = timerTask;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TimerTask getTimerTask() {
        return this.timerTask;
    }

}
