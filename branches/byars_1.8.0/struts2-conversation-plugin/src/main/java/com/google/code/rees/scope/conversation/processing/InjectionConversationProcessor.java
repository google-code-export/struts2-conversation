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
 *  $Id: InjectionConversationProcessor.java reesbyars $
 ******************************************************************************/
package com.google.code.rees.scope.conversation.processing;

import java.lang.reflect.Field;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.code.rees.scope.conversation.ConversationAdapter;
import com.google.code.rees.scope.conversation.ConversationUtil;
import com.google.code.rees.scope.conversation.configuration.ConversationClassConfiguration;
import com.google.code.rees.scope.conversation.context.ConversationContext;
import com.google.code.rees.scope.util.InjectionUtil;

/**
 * The default implementation of the {@link InjectionConversationProcessor}
 * 
 * @author rees.byars
 */
public class InjectionConversationProcessor extends SimpleConversationProcessor implements PostActionProcessor {

    private static final long serialVersionUID = 8632020943340087L;
    private static final Logger LOG = LoggerFactory.getLogger(InjectionConversationProcessor.class);
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void handleContinuing(ConversationClassConfiguration conversationConfig, ConversationAdapter conversationAdapter, Object action, ConversationContext conversationContext) {
    	Map<String, Field> actionConversationFields = conversationConfig.getFields();
        
        if (actionConversationFields != null) {
            InjectionUtil.setFieldValues(action, actionConversationFields, conversationContext);
        }
        
        conversationAdapter.addPostActionProcessor(this, conversationConfig, conversationContext.getId());
        conversationAdapter.getViewContext().put(conversationContext.getConversationName(), conversationContext.getId());
	}
	
    /**
     * {@inheritDoc}
     */
    @Override
	protected void handleEnding(ConversationClassConfiguration conversationConfig, ConversationAdapter conversationAdapter, Object action, ConversationContext conversationContext) {
    	Map<String, Field> actionConversationFields = conversationConfig.getFields();
        
        if (actionConversationFields != null) {
            InjectionUtil.setFieldValues(action, actionConversationFields, conversationContext);
        }
        
        conversationAdapter.addPostActionProcessor(new ConversationEndProcessor(), conversationConfig, conversationContext.getId());
	}
	
    /**
     * {@inheritDoc}
     */
    @Override
    protected void handleBeginning(ConversationClassConfiguration conversationConfig, ConversationAdapter conversationAdapter, Object action) {
		long maxIdleTime = conversationConfig.getMaxIdleTime(conversationAdapter.getActionId());
        
        if (LOG.isDebugEnabled()) {
            LOG.debug("Beginning new " + conversationConfig.getConversationName() + " with max idle time of " + maxIdleTime / 1000 + " seconds for action " + conversationAdapter.getActionId());
        }
        
        ConversationContext newConversationContext = ConversationUtil.begin(conversationConfig.getConversationName(), conversationAdapter, maxIdleTime);
        conversationAdapter.addPostActionProcessor(this, conversationConfig, newConversationContext.getId());
	}

    /**
     * {@inheritDoc}
     */
    @Override
    public void postProcessConversation(ConversationAdapter conversationAdapter, ConversationClassConfiguration conversationConfig, String conversationId) {

    	String conversationName = conversationConfig.getConversationName();
    	
    	if (LOG.isDebugEnabled()) {
            LOG.debug("Performing post-processing of  " + conversationName + " with ID of " + conversationId + "...");
        }
    	
        Object action = conversationAdapter.getAction();

        Map<String, Field> actionConversationFields = conversationConfig.getFields();

        if (actionConversationFields != null) {

            if (LOG.isDebugEnabled()) {
                LOG.debug("Getting conversation fields for " + conversationName + " following execution of action " + conversationAdapter.getActionId());
            }
            
            Map<String, Object> conversationContext = conversationAdapter.getConversationContext(conversationName, conversationId);
            
            if (conversationContext != null) {
            	conversationContext.putAll(InjectionUtil.getFieldValues(action, actionConversationFields));
            }

        }
        
        if (LOG.isDebugEnabled()) {
            LOG.debug("...completed post-processing of  " + conversationName + " with ID of " + conversationId + ".");
        }
        
    }

}
