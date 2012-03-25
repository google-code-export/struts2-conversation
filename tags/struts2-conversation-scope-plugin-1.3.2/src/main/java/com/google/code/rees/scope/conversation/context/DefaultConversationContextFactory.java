package com.google.code.rees.scope.conversation.context;

/**
 * The default implementation of the {@link ConversationContextFactory}
 * 
 * @author rees.byars
 * 
 */
public class DefaultConversationContextFactory implements
        ConversationContextFactory {

    private static final long serialVersionUID = 5527890036398455557L;

    protected long conversationDuration = DEFAULT_CONVERSATION_DURATION;

    /**
     * {@inheritDoc}
     */
    @Override
    public void setConversationDuration(long duration) {
        this.conversationDuration = duration;
    }

    /**
     * {@inheritDoc}
     * 
     * The implementation of the context is the
     * {@link DefaultConversationContext}
     */
    @Override
    public ConversationContext create(String conversationName,
            String conversationId) {
        return new DefaultConversationContext(conversationName, conversationId,
                conversationDuration);
    }

}
