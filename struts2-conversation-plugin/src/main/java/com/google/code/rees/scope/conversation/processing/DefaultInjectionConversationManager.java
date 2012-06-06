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
 *  $Id: DefaultInjectionConversationManager.java reesbyars $
 ******************************************************************************/
package com.google.code.rees.scope.conversation.processing;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.code.rees.scope.conversation.ConversationAdapter;
import com.google.code.rees.scope.conversation.ConversationUtil;
import com.google.code.rees.scope.conversation.configuration.ConversationConfiguration;
import com.google.code.rees.scope.conversation.context.ConversationContext;
import com.google.code.rees.scope.conversation.exceptions.ConversationException;
import com.google.code.rees.scope.util.ScopeUtil;

/**
 * The default implementation of the {@link InjectionConversationManager}
 * 
 * @author rees.byars
 */
public class DefaultInjectionConversationManager extends SimpleConversationManager implements InjectionConversationManager, ConversationPostProcessor {

    private static final long serialVersionUID = 8632020943340087L;
    private static final Logger LOG = LoggerFactory.getLogger(DefaultInjectionConversationManager.class);

    /**
     * {@inheritDoc}
     * @throws ConversationException 
     */
    @Override
    protected void processConversation(ConversationConfiguration conversationConfig, ConversationAdapter conversationAdapter, Object action) throws ConversationException {

        String actionId = conversationAdapter.getActionId();
        String conversationName = conversationConfig.getConversationName();
        String conversationId = conversationAdapter.getRequestContext().get(conversationName);

        if (LOG.isDebugEnabled()) {
            LOG.debug("Processing request for " + conversationName + " and action " + actionId + " of class " + action.getClass());
        }

        if (conversationId != null) {

            if (conversationConfig.containsAction(actionId)) {

                Map<String, Object> conversationContext = conversationAdapter.getConversationContext(conversationName, conversationId);

                if (LOG.isDebugEnabled()) {
                    LOG.debug("The action is a conversation member.  Processing with context:  " + conversationContext);
                }

                if (conversationContext != null) {

                    Map<String, Field> actionConversationFields = conversationConfig.getFields();
                    
                    if (actionConversationFields != null) {
                        ScopeUtil.setFieldValues(action, actionConversationFields, conversationContext);
                    }
                    
                } else {
                	
                	this.handleInvalidId(conversationName, conversationId);
                	
                }

                if (conversationConfig.isEndAction(actionId)) {
                    conversationAdapter.addPostProcessor(new ConversationEndProcessor(), conversationConfig, conversationId);
                } else {
                    conversationAdapter.addPostProcessor(this, conversationConfig, conversationId);
                    conversationAdapter.getViewContext().put(conversationName, conversationId);
                }
            }
        } else if (conversationConfig.isBeginAction(actionId)) {
            
            long maxIdleTime = conversationConfig.getMaxIdleTime(actionId);
            
            if (LOG.isDebugEnabled()) {
                LOG.debug("Beginning new " + conversationName+ " with max idle time of " + maxIdleTime / 1000 + " seconds for action " + actionId);
            }
            
            ConversationContext newConversationContext = ConversationUtil.begin(conversationName, conversationAdapter, maxIdleTime);
            conversationId = newConversationContext.getId();
            conversationAdapter.addPostProcessor(this, conversationConfig, conversationId);
            
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void postProcessConversation(ConversationAdapter conversationAdapter, ConversationConfiguration conversationConfig, String conversationId) {

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
            	conversationContext.putAll(ScopeUtil.getFieldValues(action, actionConversationFields));
            }

        }
        
        if (LOG.isDebugEnabled()) {
            LOG.debug("...completed post-processing of  " + conversationName + " with ID of " + conversationId + ".");
        }
        
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void injectConversationFields(Object target, ConversationAdapter conversationAdapter) {
        Collection<ConversationConfiguration> actionConversationConfigs = this.configurationProvider.getConfigurations(target.getClass());
        if (actionConversationConfigs != null) {
            for (ConversationConfiguration conversation : actionConversationConfigs) {
                String conversationName = conversation.getConversationName();
                String conversationId = conversationAdapter.getRequestContext().get(conversationName);
                if (conversationId != null) {
                    Map<String, Object> conversationContext = conversationAdapter.getConversationContext(conversationName, conversationId);
                    if (conversationContext != null) {
                        Map<String, Field> actionConversationFields = conversation.getFields();
                        if (actionConversationFields != null) {
                            ScopeUtil.setFieldValues(target, actionConversationFields, conversationContext);
                        }
                    }
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void extractConversationFields(Object target, ConversationAdapter conversationAdapter) {
        Collection<ConversationConfiguration> actionConversationConfigs = this.configurationProvider.getConfigurations(target.getClass());
        if (actionConversationConfigs != null) {
            for (ConversationConfiguration conversation : actionConversationConfigs) {

                Map<String, Field> actionConversationFields = conversation.getFields();
                String conversationName = conversation.getConversationName();
                String conversationId = conversationAdapter.getRequestContext().get(conversationName);

                if (conversationId != null) {

	                if (actionConversationFields != null) {
	
	                    Map<String, Object> conversationContext = conversationAdapter.getConversationContext(conversationName, conversationId);
	                    conversationContext.putAll(ScopeUtil.getFieldValues(target, actionConversationFields));
	                }
	
	                conversationAdapter.getViewContext().put(conversationName, conversationId);
                
                }
            }
        }
    }

}
