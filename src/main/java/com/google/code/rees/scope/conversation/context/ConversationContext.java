package com.google.code.rees.scope.conversation.context;

import java.util.TimerTask;

import com.google.code.rees.scope.util.MonitoredContext;

/**
 * The context in which the conversation-scoped beans are placed. There
 * is a separate context for each instance of each conversation.
 * 
 * @author rees.byars
 * 
 */
public interface ConversationContext extends MonitoredContext<String, Object> {

    /**
     * The name of the conversation for which this context represents an
     * instance
     * 
     * @return
     */
    public String getConversationName();

    /**
     * Set the context's timer task. Used internally for managing cleanup of
     * stale conversations
     * 
     * @param timerTask
     */
    public void setTimerTask(TimerTask timerTask);

    /**
     * Get the context's timer task. Used internally for managing cleanup of
     * stale conversations
     */
    public TimerTask getTimerTask();

}
