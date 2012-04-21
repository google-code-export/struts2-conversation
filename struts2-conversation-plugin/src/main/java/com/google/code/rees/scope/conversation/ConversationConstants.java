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
 *  $Id: ConversationConstants.java reesbyars $
 ******************************************************************************/
package com.google.code.rees.scope.conversation;

/**
 * Constants used for conversation management
 * 
 * @author rees.byars
 */
public class ConversationConstants {
	
	/**
	 * 20
	 */
	public static final int DEFAULT_MAXIMUM_NUMBER_OF_A_GIVEN_CONVERSATION = 20;
	
	/**
	 * 8 hours
	 */
	public static final long DEFAULT_CONVERSATION_MAX_IDLE_TIME = 28800000;
	
    public static final String CONVERSATION_CONTEXT_MANAGER_KEY = "rees.scope.conversation.context.manager.key";
    public static final String CONVERSATION_NAME_SESSION_MAP_SUFFIX = "-conversation";
    public static final String DEFAULT_CONTROLLER_SUFFIX = "Controller";
    public static final String TEMPORARY_CONTEXT_NAME = "rees.scope.temporary.context";
}
