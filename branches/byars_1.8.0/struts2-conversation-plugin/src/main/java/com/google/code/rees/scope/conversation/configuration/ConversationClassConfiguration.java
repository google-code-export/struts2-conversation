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
 *  $Id: ConversationClassConfiguration.java reesbyars $
 ******************************************************************************/
package com.google.code.rees.scope.conversation.configuration;

import java.lang.reflect.Field;
import java.util.Map;

import com.google.code.rees.scope.conversation.ConversationAdapter;
import com.google.code.rees.scope.conversation.processing.ConversationProcessor;

/**
 * This class is used to cache the fields and action IDs for a single
 * class for a single conversation.
 * 
 * @see {@link ConversationConfigurationProvider}
 * @see {@link ConversationProcessor}
 * 
 * @author rees.byars
 * 
 */
public interface ConversationClassConfiguration {

    /**
     * Add a field to the configuration
     * 
     * @param name
     * @param field
     */
    public void addField(String name, Field field);

    /**
     * Get the cached fields for conversation
     * 
     * @return
     */
    public Map<String, Field> getFields();

    /**
     * Add an actionId for an intermediate action
     * 
     * @see {@link ConversationAdapter#getActionId()}
     * @see {@link ConversationArbitrator#getName(Method)}
     */
    public void addAction(String actionId, String preActionExpression, String postActionExpression, String postViewExpression);

    /**
     * Add an actionId for a begin action
     * 
     * @see {@link ConversationAdapter#getActionId()}
     * @see {@link ConversationArbitrator#getName(Method)}
     */
    public void addBeginAction(String actionId, String preActionExpression, String postActionExpression, String postViewExpression, long maxIdleTimeMillis, String maxIdleTime, int maxInstances, boolean transactional);

    /**
     * Add an actionId for an end action
     * 
     * @see {@link ConversationAdapter#getActionId()}
     * @see {@link ConversationArbitrator#getName(Method)}
     */
    public void addEndAction(String actionId, String preActionExpression, String postActionExpression, String postViewExpression, boolean endAfterView);

    /**
     * Indicates whether the actionId identifies the action as an intermediate
     * member for this conversation
     * 
     * @see {@link ConversationAdapter#getActionId()}
     * @see {@link ConversationArbitrator#getName(Method)}
     * @param actionId
     * @return
     */
    public boolean containsAction(String actionId);

    /**
     * Indicates whether the actionId identifies the action as a begin
     * member for this conversation
     * 
     * @see {@link ConversationAdapter#getActionId()}
     * @see {@link ConversationArbitrator#getName(Method)}
     * @param actionId
     * @return
     */
    public boolean isBeginAction(String actionId);

    /**
     * Indicates whether the actionId identifies the action as an end
     * member for this conversation
     * 
     * @see {@link ConversationAdapter#getActionId()}
     * @see {@link ConversationArbitrator#getName(Method)}
     * @param actionId
     * @return
     */
    public boolean isEndAction(String actionId);
    
    /**
     * given the id of an end action, returns true if the ConversationContext should be removed after the view is rendered
     * @param actionId
     * @return
     */
    public boolean endAfterView(String actionId);
    
    /**
     * given the begin action's ID, returns the max idle time (in milliseconds) for the conversation created by that action
     * 
     * @param beginActionId
     * @return
     */
    public long getMaxIdleTime(String beginActionId);
    
    /**
     * 
     * @param beginActionId
     * @return
     */
    public String getMaxIdleTimeExpression(String beginActionId);
    
    /**
     * given the begin action's ID, returns the max number of instances
     * 
     * @param beginActionId
     * @return
     */
    public int getMaxInstances(String beginActionId);
    
    /**
     * 
     * @param acitonId
     * @return
     */
    public String getPreActionExpression(String actionId);
    
    /**
     * 
     * @param acitonId
     * @return
     */
    public String getPostActionExpression(String actionId);
    
    /**
     * 
     * @param acitonId
     * @return
     */
    public String getPostViewExpression(String actionId);
    
    /**
     * 
     * @return
     */
    public boolean isTransactional(String beginActionId);

    /**
     * Returns the name of the conversation that this configuration is for
     * 
     * @return
     */
    public String getConversationName();
}
