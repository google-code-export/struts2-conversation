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
 *  $Id: BeginConversation.java reesbyars $
 ******************************************************************************/
package com.google.code.rees.scope.conversation.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.google.code.rees.scope.conversation.ConversationConstants;

/**
 * Denotes a method as a conversation-initiating method. Depending on
 * the {@link com.google.code.rees.scope.conversation.configuration.ConversationArbitrator
 * ConversationArbitrator} being used, the convention of
 * using the {@link ConversationController} annotation and beginning the name of
 * an action method with "begin" can be used instead of this annotation.
 * 
 * @author rees.byars
 * 
 * @see #conversations()
 * 
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BeginConversation {

    /**
     * The conversations for which this method will act as an begin-point.
     * <p>
     * In an action class annotated with the {@link ConversationController}
     * annotation, the {@link #conversations()} field will default to that of
     * the controller's conversations if left blank. If not in a
     * ConversationController, then the conversations field must be specified in
     * order to associate this begin point.
     */
    public abstract String[] conversations() default {};
    
    /**
     * The idle time after which this conversation will be timed out and cleaned up, in milliseconds.  This will override
     * all other settings (such as in a struts.xml).
     */
    public abstract long maxIdleTimeMillis() default ConversationConstants.DEFAULT_CONVERSATION_MAX_IDLE_TIME;
    
    /**
     * The max allowed number of instances of this conversation.  When the max is exceeded, the least recently
     * active conversation is discarded.  This will override
     * all other settings (such as in a struts.xml).
     */
    public abstract int maxInstances() default ConversationConstants.DEFAULT_MAXIMUM_NUMBER_OF_A_GIVEN_CONVERSATION;

}
