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
package com.github.overengineer.scope.conversation.configuration;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;

import com.github.overengineer.scope.conversation.ConversationAdapter;

/**
 * This class is used by a {@link ConversationConfigurationProvider} to
 * determine which field and method members to associate with
 * conversations.
 *
 * @author rees.byars
 */
public interface ConversationArbitrator extends Serializable {

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
     * @param clazz
     * @param method
     * @return
     * @see {@link com.github.overengineer.scope.conversation.annotations.ConversationAction
     *      ConversationAction}
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
     * @param method
     * @return
     * @see {@link ConversationAdapter#getActionId()}
     */
    public String getName(Method method);

    /**
     * returns a collection of conversation names for the method as a begin
     * point
     *
     * @param clazz
     * @param method
     * @return
     * @see {@link com.github.overengineer.scope.conversation.annotations.BeginConversation
     *      BeginConversation}
     */
    public Collection<String> getBeginConversations(Class<?> clazz, Method method);

    /**
     * returns a collection of conversation names for the method as an end point
     *
     * @param clazz
     * @param method
     * @return
     * @see {@link com.github.overengineer.scope.conversation.annotations.EndConversation
     *      EndConversation}
     */
    public Collection<String> getEndConversations(Class<?> clazz, Method method);

}
