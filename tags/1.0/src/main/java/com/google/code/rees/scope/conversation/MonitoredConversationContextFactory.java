package com.google.code.rees.scope.conversation;

import java.util.Map;
import java.util.Timer;

import com.google.code.rees.scope.HashMonitoredContext;
import com.google.code.rees.scope.MonitoredContext;

/**
 * Implements the {@link ConversationContextFactory} using
 * {@link MonitoredContext MonitoredContexts} for the conversation contexts
 * 
 * @author rees.byars
 */
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

    /**
     * Set the frequency for monitoring contexts. Defaults to
     * {@link #DEFAULT_MONITOR_FREQUENCY}
     * 
     * @param monitoringFrequency
     */
    public void setMonitoringFrequency(long monitoringFrequency) {
        this.monitoringFrequency = monitoringFrequency;
    }

    /**
     * Set the duration to be used by
     * {@link #createConversationContext(String, Map)} Defaults to
     * {@link #DEFAULT_CONVERSATION_DURATION}
     * 
     * @param conversationDuration
     */
    public void setConversationDuration(long conversationDuration) {
        this.conversationDuration = conversationDuration;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Object> createConversationContext(String conversationId,
            Map<String, Object> sessionContext) {
        return this.createConversationContext(conversationId, sessionContext,
                this.conversationDuration);
    }

    /**
     * Similar to {@link #createConversationContext(String, Map)}, but allows
     * for dynamically variable durations. Could be used by a
     * {@link ConversationAdapter} that uses some mechanism for
     * assigning different durations to various conversations
     * 
     * @param conversationId
     * @param sessionContext
     * @param duration
     * @return
     */
    public Map<String, Object> createConversationContext(String conversationId,
            Map<String, Object> sessionContext, long duration) {
        MonitoredContext<String, Object> conversationContext = new HashMonitoredContext<String, Object>();
        conversationContext.init(conversationId, sessionContext, duration);
        this.timer.scheduleAtFixedRate(conversationContext.getTimerTask(),
                this.monitoringFrequency, this.monitoringFrequency);
        return conversationContext;
    }

}
