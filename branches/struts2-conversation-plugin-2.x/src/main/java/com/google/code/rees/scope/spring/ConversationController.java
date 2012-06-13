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
package com.google.code.rees.scope.spring;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.stereotype.Controller;

/**
 * Combines the Spring MVC {@link Controller} with the
 * {@link com.google.code.rees.scope.conversation.annotations.ConversationController
 * ConversationController}.
 * 
 * @author rees.byars
 */
@Target({ java.lang.annotation.ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Controller
@Documented
public @interface ConversationController {

	public final static String DEFAULT_VALUE = "";

	public abstract String value() default DEFAULT_VALUE;

	/**
	 * @see {@link com.google.code.rees.scope.conversation.annotations.ConversationController#conversations()
	 *      ConversationController.conversations()}
	 * @return
	 */
	public abstract String[] conversations() default {};
}
