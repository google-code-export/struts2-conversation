package com.google.code.rees.scope.conversation.context;

import java.util.TimerTask;

import com.google.code.rees.scope.util.MonitoredContext;

public interface ConversationContext extends MonitoredContext<String, Object> {

    public String getConversationName();

    public void setTimerTask(TimerTask timerTask);

    public TimerTask getTimerTask();

}
