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
     * @param actionId
     */
    public void addAction(String actionId);

    /**
     * Add an actionId for a begin action
     * 
     * @see {@link ConversationAdapter#getActionId()}
     * @see {@link ConversationArbitrator#getName(Method)}
     * @param actionId
     */
    public void addBeginAction(String actionId, Long maxIdleTimeMillis);

    /**
     * Add an actionId for an end action
     * 
     * @see {@link ConversationAdapter#getActionId()}
     * @see {@link ConversationArbitrator#getName(Method)}
     * @param actionId
     */
    public void addEndAction(String actionId);

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
     * 
     * @param acitonId
     * @return
     */
    public String getPreExpression(String acitonId);
    
    /**
     * 
     * @param acitonId
     * @return
     */
    public String getPostExpression(String acitonId);
    
    /**
     * 
     * @return
     */
    public boolean isTransactional();

    /**
     * Returns the name of the conversation that this configuration is for
     * 
     * @return
     */
    public String getConversationName();
}
