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
 *  $Id: ConversationArbitrator.java reesbyars $
 ******************************************************************************/
package com.google.code.rees.scope.conversation.configuration;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;

import com.google.code.rees.scope.conversation.ConversationAdapter;

/**
 * This class is used by a {@link ConversationConfigurationProvider} to
 * determine which field and method members to associate with
 * conversations.
 * 
 * @author rees.byars
 * 
 */
public interface ConversationArbitrator extends Serializable {

    /**
     * The default suffix is "Controller". So the conversation for a class
     * named <code>MyExampleFlowController</code> would be
     * <code>my-example-flow</code>. Use this setter to use a different suffix.
     * 
     * @param suffix
     */
    public void setActionSuffix(String suffix);

    /**
     * Returns a collection of candidate conversation fields from a class.
     * Subsequent
     * calls are made to the {@link #getConversations(Class, Field)} to
     * determine
     * which, if any, conversations the field should be included with
     * 
     * @param clazz
     * @return
     */
    public Collection<Field> getCandidateConversationFields(Class<?> clazz);

    /**
     * Returns a collection of candidate conversation action methods from a
     * class. Subsequent
     * calls are made to the {@link #getConversations(Class, Method)} to
     * determine
     * which, if any, conversations the method should be included with
     * 
     * @param clazz
     * @return
     */
    public Collection<Method> getCandidateConversationMethods(Class<?> clazz);

    /**
     * Returns a collection of conversation names for the field
     * 
     * @param clazz
     * @param field
     * @return
     */
    public Collection<String> getConversations(Class<?> clazz, Field field);

    /**
     * returns a collection of conversation names for the method as an
     * intermediate point
     * 
     * @see {@link com.google.code.rees.scope.conversation.annotations.ConversationAction
     *      ConversationAction}
     * @param clazz
     * @param method
     * @return
     */
    public Collection<String> getConversations(Class<?> clazz, Method method);

    /**
     * returns the name to be used for the field as a key in the conversation
     * context
     * 
     * @param field
     * @return
     */
    public String getName(Field field);

    /**
     * returns the name to be used as an actionId
     * 
     * @see {@link ConversationAdapter#getActionId()}
     * @param method
     * @return
     */
    public String getName(Method method);

    /**
     * returns a collection of conversation names for the method as a begin
     * point
     * 
     * @see {@link com.google.code.rees.scope.conversation.annotations.BeginConversation
     *      BeginConversation}
     * @param clazz
     * @param method
     * @return
     */
    public Collection<String> getBeginConversations(Class<?> clazz, Method method);

    /**
     * returns a collection of conversation names for the method as an end point
     * 
     * @see {@link com.google.code.rees.scope.conversation.annotations.EndConversation
     *      EndConversation}
     * @param clazz
     * @param method
     * @return
     */
    public Collection<String> getEndConversations(Class<?> clazz, Method method);
}
