package com.google.code.rees.scope.conversation.processing;

import com.google.code.rees.scope.conversation.ConversationAdapter;
import com.google.code.rees.scope.conversation.configuration.ConversationClassConfiguration;

/**
 * This interface allows for post-view-processing of a conversation.  Registered via
 * {@link ConversationAdapter#addPostViewProcessor(PostViewProcessor, ConversationClassConfiguration, String)}
 * @author rees.byars
 */
public interface PostViewProcessor extends PostProcessor {
}
