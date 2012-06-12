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
 *  $Id: ConversationAction.java reesbyars $
 ******************************************************************************/
package com.google.code.rees.scope.conversation.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Denotes an action as an intermediate member a conversation.
 * <p>
 * Only works on methods that are also actions. Depending on the
 * {@link com.google.code.rees.scope.conversation.configuration.ConversationArbitrator
 * ConversationArbitrator} being used, the convention of using the
 * {@link ConversationController} can replace the need for this annotation in
 * most cases.
 * <P>
 * 
 * @author rees.byars
 * 
 * @see #conversations()
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ConversationAction {

    /**
     * The conversations for which this method is a member.
     * <p>
     * In an action class annotated with the {@link ConversationController}
     * annotation, the {@link #conversations()} field will default to that of
     * the controller's conversations if left blank. If not in a
     * ConversationController, then the conversations field must be specified in
     * order to associate this action.
     */
    public abstract String[] conversations() default {};
    
    /**
     * Specify the {@link GuardType} to be used for this action.
     * 
     * @return
     */
    public abstract GuardType guardType() default GuardType.NONE;
    
    /**
     * Specify the subset of conversations to be guarded.  This subset is only used if
     * {@link #guardType()} is set to {@link GuardType#SUBSET}.
     * @return
     */
    public abstract String[] guardedConversations() default {};

}
