package com.github.overengineer.scope.conversation.processing;

import com.github.overengineer.scope.conversation.ConversationAdapter;
import com.github.overengineer.scope.conversation.configuration.ConversationClassConfiguration;

/**
 * This interface allows for post-view-processing of a conversation.  Registered via
 * {@link ConversationAdapter#addPostViewProcessor(PostViewProcessor, ConversationClassConfiguration, String)}
 *
 * @author rees.byars
 */
public interface PostViewProcessor extends PostProcessor {
}
