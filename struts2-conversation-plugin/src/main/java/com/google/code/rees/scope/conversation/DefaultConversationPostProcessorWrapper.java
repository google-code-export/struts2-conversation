package com.google.code.rees.scope.conversation;

/**
 * The default implementation of the {@link ConversationPostProcessorWrapper}
 * 
 * @author rees.byars
 */
public class DefaultConversationPostProcessorWrapper implements
        ConversationPostProcessorWrapper {

    private static final long serialVersionUID = -8235162251071925835L;

    private transient ConversationConfiguration conversationConfig;
    private String conversationId;
    private ConversationPostProcessor postProcessor;
    private ConversationAdapter conversationAdapter;

    public DefaultConversationPostProcessorWrapper(
            ConversationAdapter conversationAdapter,
            ConversationPostProcessor postProcessor,
            ConversationConfiguration conversationConfig, String conversationId) {
        this.conversationAdapter = conversationAdapter;
        this.postProcessor = postProcessor;
        this.conversationConfig = conversationConfig;
        this.conversationId = conversationId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void postProcessConversation() {
        this.postProcessor.postProcessConversation(conversationAdapter,
                conversationConfig, conversationId);
    }

}
