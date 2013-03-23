/*******************************************************************************
 *
 *  Struts2-Conversation-Plugin - An Open Source Conversation- and Flow-Scope Solution for Struts2-based Applications
 *  =================================================================================================================
 *
 *  Copyright (C) 2012 by Rees Byars
 *  http://code.google.com/p/struts2-conversation/
 *
 * **********************************************************************************************************************
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 *  the License. You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 *  an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 *  specific language governing permissions and limitations under the License.
 *
 * **********************************************************************************************************************
 *
 *  $Id: ConversationEndProcessor.java reesbyars $
 ******************************************************************************/
package com.github.overengineer.scope.conversation.processing;

import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.overengineer.scope.conversation.ConversationAdapter;
import com.github.overengineer.scope.conversation.configuration.ConversationClassConfiguration;

/**
 * A {@link PostProcessor} used to remove a conversation context either after an action has been executed or after the view has been rendered.  Is registered
 * for execution via {@link ConversationAdapter#addPostProcessor(PostActionProcessor, ConversationClassConfiguration, String)}
 * or {@link ConversationAdapter#addPostViewProcessor(PostViewProcessor, ConversationClassConfiguration, String)}.
 *
 * @author rees.byars
 */
public class ConversationEndProcessor implements PostActionProcessor, PostViewProcessor {

    private static final long serialVersionUID = 2164492176500215404L;
    private static final Logger LOG = LoggerFactory.getLogger(ConversationEndProcessor.class);

    /**
     * Removes the conversation context
     */
    @Override
    public void postProcessConversation(ConversationAdapter conversationAdapter, ConversationClassConfiguration conversationConfig, String conversationId) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("In Conversation " + conversationConfig.getConversationName() + ", removing conversation context following conversation end.");
        }
        String conversationName = null;
        if (conversationConfig == null) {
            for (Entry<String, String> nameIdPair : conversationAdapter.getRequestContext().entrySet()) {
                if (nameIdPair.getValue().equals(conversationId)) {
                    conversationName = nameIdPair.getKey();
                    break;
                }
            }
        } else {
            conversationName = conversationConfig.getConversationName();
        }
        conversationAdapter.endConversation(conversationName, conversationId);
    }

}
