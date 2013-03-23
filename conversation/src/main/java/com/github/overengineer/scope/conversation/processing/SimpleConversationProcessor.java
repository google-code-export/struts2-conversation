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
 *  $Id: SimpleConversationProcessor.java reesbyars $
 ******************************************************************************/
package com.github.overengineer.scope.conversation.processing;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.overengineer.scope.container.Component;
import com.github.overengineer.scope.conversation.ConversationAdapter;
import com.github.overengineer.scope.conversation.ConversationUtil;
import com.github.overengineer.scope.conversation.annotations.ConversationField;
import com.github.overengineer.scope.conversation.configuration.ConversationClassConfiguration;
import com.github.overengineer.scope.conversation.configuration.ConversationConfigurationProvider;
import com.github.overengineer.scope.conversation.context.ConversationContext;
import com.github.overengineer.scope.conversation.exceptions.ConversationException;
import com.github.overengineer.scope.conversation.exceptions.ConversationIdException;

/**
 * A simple yet effective implementation of {@link ConversationProcessor} that
 * manages conversation life cycles, but does not inject
 * {@link ConversationField ConversationFields}. Ideal for using in cases such
 * as when field injection will be handled by Spring.
 *
 * @author rees.byars
 */
public class SimpleConversationProcessor implements ConversationProcessor {

    private static final long serialVersionUID = -518452439785782433L;
    private static final Logger LOG = LoggerFactory.getLogger(SimpleConversationProcessor.class);

    protected ConversationConfigurationProvider configurationProvider;

    @Component
    public void setConfigurationProvider(ConversationConfigurationProvider configurationProvider) {
        this.configurationProvider = configurationProvider;
    }

    /**
     * {@inheritDoc}
     *
     * @throws ConversationException
     */
    @Override
    public void processConversations(ConversationAdapter conversationAdapter) throws ConversationException {

        if (LOG.isDebugEnabled()) {
            LOG.debug("Beginning processing of conversations...");
        }

        try {

            Object action = conversationAdapter.getAction();
            Class<?> actionClass = action.getClass();


            Collection<ConversationClassConfiguration> actionConversationConfigs = this.configurationProvider.getConfigurations(actionClass);
            if (actionConversationConfigs != null) {
                for (ConversationClassConfiguration conversationConfig : actionConversationConfigs) {
                    processConversation(conversationConfig, conversationAdapter, action);
                }
            }

        } catch (ConversationException ce) {

            //just catching to re-throw previously thrown ConversationExceptions instead of "generifying" them
            throw ce;

        } catch (Exception e) {

            LOG.error("An exception occurred while processing the conversations", e);
            throw new ConversationException(e.getMessage());
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("...processing of conversations complete.");
        }
    }

    protected void processConversation(ConversationClassConfiguration conversationConfig, ConversationAdapter conversationAdapter, Object action) throws ConversationException {

        String actionId = conversationAdapter.getActionId();
        String conversationName = conversationConfig.getConversationName();
        String conversationId = conversationAdapter.getRequestContext().get(conversationName);

        if (conversationId != null) {
            ConversationContext conversationContext = conversationAdapter.getConversationContext(conversationName, conversationId);
            if (conversationContext != null) {
                if (conversationConfig.containsAction(actionId)) {
                    if (conversationConfig.isEndAction(actionId)) {
                        this.handleEnding(conversationConfig, conversationAdapter, conversationContext);
                    } else {
                        this.handleContinuing(conversationConfig, conversationAdapter, conversationContext);
                    }
                }
            } else {
                this.handleInvalidId(conversationName, conversationId);
            }
        } else if (conversationConfig.isBeginAction(actionId)) {
            this.handleBeginning(actionId, conversationConfig, conversationAdapter);
        }
    }

    protected void handleContinuing(ConversationClassConfiguration conversationConfig, ConversationAdapter conversationAdapter, ConversationContext conversationContext) {

        if (LOG.isDebugEnabled()) {
            LOG.debug("Continuing conversation [{}] with ID [{}]", conversationConfig.getConversationName(), conversationContext.getId());
        }

        conversationAdapter.getViewContext().put(conversationContext.getConversationName(), conversationContext.getId());
    }

    protected void handleEnding(ConversationClassConfiguration conversationConfig, ConversationAdapter conversationAdapter, ConversationContext conversationContext) {

        if (LOG.isDebugEnabled()) {
            LOG.debug("Ending conversation [{}] with ID [{}]", conversationConfig.getConversationName(), conversationContext.getId());
        }

        conversationAdapter.addPostActionProcessor(new ConversationEndProcessor(), conversationConfig, conversationContext.getId());
    }

    protected void handleBeginning(String actionId, ConversationClassConfiguration conversationConfig, ConversationAdapter conversationAdapter) {
        long maxIdleTime = conversationConfig.getMaxIdleTime(actionId);

        if (LOG.isDebugEnabled()) {
            LOG.debug("Beginning new [{}] with max idle time of [{}] seconds for action [{}]", conversationConfig.getConversationName(), maxIdleTime / 1000, conversationAdapter.getActionId());
        }

        ConversationUtil.begin(conversationConfig.getConversationName(), conversationAdapter, maxIdleTime, conversationConfig.getMaxInstances(actionId));
    }

    protected void handleInvalidId(String conversationName, String conversationId) throws ConversationIdException {
        String idExceptionMessage = "The following conversation name and id pair did not return an active ConversationContext:  (name: " + conversationName + "|id:  " + conversationId + ").  This is likely due to the conversation having ended or expired.";
        LOG.warn(idExceptionMessage);
        throw new ConversationIdException(idExceptionMessage, conversationName, conversationId);
    }

}
