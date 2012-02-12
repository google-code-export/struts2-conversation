package com.google.code.rees.scope.conversation;

import java.util.Map;
import java.util.Timer;

import com.google.code.rees.scope.HashMonitoredContext;
import com.google.code.rees.scope.MonitoredContext;

public class MonitoredConversationContextFactory implements
        ConversationContextFactory {

    private static final long serialVersionUID = -4364749054093457093L;

    /**
     * 8 hours
     */
    public static final long DEFAULT_CONVERSATION_DURATION = 28800000;

    /**
     * 5 minutes
     */
    public static final long DEFAULT_MONITOR_FREQUENCY = 300000;

    protected long conversationDuration;
    protected long monitoringFrequency;
    protected Timer timer;

    public MonitoredConversationContextFactory() {
        this.timer = new Timer();
        this.conversationDuration = DEFAULT_CONVERSATION_DURATION;
        this.monitoringFrequency = DEFAULT_MONITOR_FREQUENCY;
    }

    public MonitoredConversationContextFactory(long conversationDuration,
            long monitoringFrequency) {
        this.timer = new Timer();
        this.conversationDuration = conversationDuration;
        this.monitoringFrequency = monitoringFrequency;
    }

    public void setMonitoringFrequency(long monitoringFrequency) {
        this.monitoringFrequency = monitoringFrequency;
    }

    public void setConversationDuration(long conversationDuration) {
        this.conversationDuration = conversationDuration;
    }

    @Override
    public Map<String, Object> createConversationContext(String conversationId,
            Map<String, Object> sessionContext) {
        return this.createConversationContext(conversationId, sessionContext,
                this.conversationDuration);
    }

    public Map<String, Object> createConversationContext(String conversationId,
            Map<String, Object> sessionContext, long duration) {
        MonitoredContext<String, Object> conversationContext = new HashMonitoredContext<String, Object>();
        conversationContext.init(conversationId, sessionContext, duration);
        this.timer.scheduleAtFixedRate(conversationContext.getTimerTask(),
                this.monitoringFrequency, this.monitoringFrequency);
        return conversationContext;
    }

}
