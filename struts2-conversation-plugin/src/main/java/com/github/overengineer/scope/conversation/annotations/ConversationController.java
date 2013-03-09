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
 *  $Id: ConversationController.java reesbyars $
 ******************************************************************************/
package com.github.overengineer.scope.conversation.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to denote an action class is conversation-scoped.
 * <p>
 * When this annotation is used on an action class, then actions within the
 * class default to be members of the conversations specified by this
 * annotation.
 * <p>
 * Depending on the
 * {@link com.github.overengineer.scope.conversation.configuration.ConversationArbitrator
 * ConversationArbitrator} being used, by convention any action methods that
 * begin with the word "begin" or "end" are treated as {@link BeginConversation}
 * and {@link EndConversation} methods, respectively.
 * <p>
 * If no conversations are specified, then the name of the conversation is
 * derived from the controller class's name minus the specified controller
 * suffix. The default suffix is "Controller". So the conversation for a class
 * named <code>MyExampleFlowController</code> would be
 * <code>my-example-flow</code>. The action suffix can be set as a property on
 * the ConversationArbitrator.
 * 
 * @author rees.byars
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ConversationController {

    public final static String DEFAULT_VALUE = "DEFAULT_VALUE";

    /**
     * Used to specify a single conversation for the controller
     * <p>
     * If no conversations are specified, then the name of the conversation is
     * derived from the controller class's name minus the specified controller
     * suffix. The default suffix is "Controller". So the conversation for a
     * class named <code>MyExampleFlowController</code> would be
     * <code>my-example-flow</code>. The action suffix can be set as a property
     * on the ConversationArbitrator.
     * 
     * 
     */
    public abstract String value() default DEFAULT_VALUE;

    /**
     * Used to specify multiple conversations for the controller
     * <p>
     * If no conversations are specified, then the name of the conversation is
     * derived from the controller class's name minus the specified controller
     * suffix. The default suffix is "Controller". So the conversation for a
     * class named <code>MyExampleFlowController</code> would be
     * <code>my-example-flow</code>. The action suffix can be set as a property
     * on the ConversationArbitrator.
     */
    public abstract String[] conversations() default {};

}
