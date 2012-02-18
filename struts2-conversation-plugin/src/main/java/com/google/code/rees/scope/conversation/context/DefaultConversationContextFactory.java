package com.google.code.rees.scope.conversation.context;

public class DefaultConversationContextFactory implements
        ConversationContextFactory {

    private static final long serialVersionUID = 5527890036398455557L;

    protected long conversationDuration = DEFAULT_CONVERSATION_DURATION;

    @Override
    public void setConversationDuration(long duration) {
        this.conversationDuration = duration;
    }

    @Override
    public ConversationContext create(String conversationId) {
        return new DefaultConversationContext(conversationId,
                conversationDuration);
    }

}
