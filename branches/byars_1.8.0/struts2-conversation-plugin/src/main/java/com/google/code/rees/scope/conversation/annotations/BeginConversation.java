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
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

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
     * The idle time after which this conversation will be timed out and cleaned up, in milliseconds.
     */
    public abstract long maxIdleTimeMillis() default -1L;
    
    /**
     * An expression that returns the idle time after which this conversation will be timed out and cleaned up.
     * <p>
     * Two types of expressions are supported:  a simple, static expression indicating days, hours, etc. and a dynamic
     * expression that will be evaluated using the configured {@link com.google.code.rees.scope.expression.Eval Eval}.
     * <p>
     * The following valid examples indicate the simple syntax that can be used:
     * <ul>
     * <li><code>"3d 8h 30m 15s"</code></li>
     * <li><code>"36h 15s"</code></li>
     * <li><code>"30m"</code></li>
     * <li><code>"1d"</code></li>
     * <li><code>"45s"</code></li>
     * </ul>
     * For the dynamic expressions, an expression like <code>${timeout}</code> could be used to call getTimeout() 
     * on the action class or model (whichever is the root object).  All that is required is that the result is of type long or Long
     * 
     */
    public abstract String maxIdleTime() default "";
    
    /**
     * The max number of instances allowed for this conversation.
     */
    public abstract int maxInstances() default ConversationConstants.DEFAULT_MAXIMUM_NUMBER_OF_A_GIVEN_CONVERSATION;
    
    /**
     * if set to <code>true</code>, the framework will manage opening and closing a JPA transaction when the conversation begin and end/expires, respectively.
     * the default value is <code>false</code>.
     */
    public abstract boolean transactional() default false;
    
    /**
     * An expression that will be evaluated against the conversations alive on the request using the configured {@link com.google.code.rees.scope.expression.Eval Eval}
     * prior to the action execution
     * @return
     */
    public abstract String preActionExpression() default "";
    
    /**
     * An expression that will be evaluated against the conversations alive after action execution but before view rendering using the configured {@link com.google.code.rees.scope.expression.Eval Eval}
     * @return
     */
    public abstract String postActionExpression() default "";
    
    /**
     * An expression that will be evaluated against the conversations alive after action execution and after view rendering using the configured {@link com.google.code.rees.scope.expression.Eval Eval}
     * @return
     */
    public abstract String postViewExpression() default "";

}
