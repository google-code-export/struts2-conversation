package com.google.code.rees.scope.conversation;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * This class is used to adapt/integrate the major components of the
 * conversation management
 * to control frameworks such as Struts2 and Spring MVC or any other framework.
 * <p>
 * By employing the adapter pattern, the conversation management aspects are
 * separated from the details of the control framework and separated even from
 * the underlying request and session mechanisms.
 * 
 * @author rees.byars
 * 
 */
public abstract class ConversationAdapter implements Serializable {

    private static final long serialVersionUID = -8006640931436858515L;
    protected final static ConversationContextFactory conversationContextFactory = new MonitoredConversationContextFactory();
    protected static ThreadLocal<ConversationAdapter> conversationAdapter = new ThreadLocal<ConversationAdapter>();
    protected Map<String, String> viewContext = new HashMap<String, String>();
    protected ConversationPostProcessorWrapperFactory postProcessorFactory = new DefaultConversationPostProcessorWrapperFactory();
    protected Collection<ConversationPostProcessorWrapper> postProcessors = new HashSet<ConversationPostProcessorWrapper>();

    public ConversationAdapter() {
        conversationAdapter.set(this);
    }

    /**
     * The controller instance, such as a Struts2 action class or a Spring MVC
     * controller
     * 
     * @return
     */
    public abstract Object getAction();

    /**
     * A string identifying the current action. The convention employed
     * by the {@link DefaultConversationArbitrator} is the name of the
     * controller method being executed.
     * 
     * @return
     */
    public abstract String getActionId();

    /**
     * Returns a session-scoped map.
     * 
     * @return
     */
    public abstract Map<String, Object> getSessionContext();

    /**
     * Returns a map containing, at a minimum, conversation name/id key/value
     * pairs
     * for the current request.
     * 
     * @return
     */
    public abstract Map<String, String> getRequestContext();

    /**
     * Returns a map, associated with the current session, the contains
     * the values for an instance of a conversation.
     * 
     * @param conversationId
     * @return
     */
    public Map<String, Object> createConversationContext(String conversationId) {
        return conversationContextFactory.createConversationContext(
                conversationId, getSessionContext());
    }

    /**
     * Returns a map that is used to place conversation name/id key/value pairs
     * for placing in the view context (the view context being, for instance, a
     * JSP).
     * 
     * @return
     */
    public Map<String, String> getViewContext() {
        return this.viewContext;
    }

    /**
     * Add a {@link ConversationPostProcessor} that is guaranteed to be
     * executed after action execution by a call to
     * {@link #executePostProcessors()}
     * 
     * @param postProcessor
     * @param conversationConfig
     * @param conversationId
     */
    public void addPostProcessor(ConversationPostProcessor postProcessor,
            ConversationConfiguration conversationConfig, String conversationId) {
        ConversationPostProcessorWrapper wrapper = this.postProcessorFactory
                .create(this, postProcessor, conversationConfig, conversationId);
        this.postProcessors.add(wrapper);
    }

    /**
     * Executes all {@link ConversationPostProcessor ConversationPostProcessors}
     * that have been added using
     * {@link #addPostProcessor(ConversationPostProcessor, ConversationConfiguration, String)}
     */
    public void executePostProcessors() {
        for (ConversationPostProcessorWrapper postProcessor : this.postProcessors) {
            postProcessor.postProcessConversation();
        }
    }

    /**
     * Set the {@link ThreadLocal} ConversationAdapter for use with the current
     * request
     * 
     * @param adapter
     */
    public static void setAdapter(ConversationAdapter adapter) {
        conversationAdapter.set(adapter);
    }

    /**
     * Get the {@link ThreadLocal} ConversationAdapter associated with the
     * current request
     * 
     * @return
     */
    public static ConversationAdapter getAdapter() {
        return conversationAdapter.get();
    }
}
