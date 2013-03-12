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
 *  $Id: StrutsScopeConstants.java reesbyars $
 ******************************************************************************/
package com.github.overengineer.scope.struts2;

/**
 * The Struts2 scope constants.
 * 
 * @author rees.byars
 */
public interface StrutsScopeConstants {
	
	//Configuration properties
	public static final String REQUIRE_FOLLOWS_CONVENTION = "struts.scope.followsConvention";
	
	//Map keys
	public static final String CONVERSATION_ID_MAP_STACK_KEY = "conversationIdMapStackKey";
	public static final String SESSION_FIELD_MAP_KEY = "session.field.map";
	
	interface TypeKeys {
		public static final String SCOPE_CONTAINER = "struts.scope.container";
		public static final String ACTION_PROVIDER = "struts.scope.actionProvider";
	    public static final String CONVERSATION_ARBITRATOR = "struts.conversation.arbitrator";
	    public static final String CONVERSATION_CONFIG_PROVIDER= "struts.conversation.configurationProvider";
	    public static final String CONVERSATION_PROCESSOR = "struts.conversation.processor";
	    public static final String CONVERSATION_PROPERTIES = "struts.conversation.properties";
	    public static final String CONVERSATION_CONTEXT_FACTORY = "struts.conversation.contextFactory";
	    public static final String CONVERSATION_CONTEXT_MANAGER_PROVIDER = "struts.conversation.contextManagerProvider";
	    public static final String SESSION_MANAGER = "struts.session.sessionManager";
	    public static final String SESSION_CONFIG_PROVIDER = "struts.session.configurationProvider";
	    public final static String EXPRESSION_EVAL = "conversation.expression.eval.provider";
	}
    
}
