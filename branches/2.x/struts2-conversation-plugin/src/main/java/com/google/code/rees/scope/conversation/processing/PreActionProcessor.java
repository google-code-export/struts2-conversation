package com.google.code.rees.scope.conversation.processing;

import com.google.code.rees.scope.conversation.ConversationAdapter;
import com.google.code.rees.scope.conversation.configuration.ConversationClassConfiguration;

/**
 * (don't let the name fool you.  it's still a {@link PostProcessor} - it is executed after the main conversation processing)
 * <p/>
 * This interface allows for pre-action-processing of a conversation.  Registered via
 * {@link ConversationAdapter#addPreActionProcessor(PreActionProcessor, ConversationClassConfiguration, String)}
 * @author rees.byars
 */
public interface PreActionProcessor extends PostProcessor{
}
