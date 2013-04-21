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
public interface ConversationConstants {
	
	/**
	 * 20
	 */
	int DEFAULT_MONITORING_THREAD_POOL_SIZE = 20;
	
	/**
	 * 20
	 */
	int DEFAULT_MAXIMUM_NUMBER_OF_A_GIVEN_CONVERSATION = 20;
	
	/**
	 * 8 hours
	 */
	long DEFAULT_CONVERSATION_MAX_IDLE_TIME = 28800000;
	
	/**
	 * used as a key to identify the {@link com.google.code.rees.scope.conversation.context.ConversationContextManager ConversationContextManager} 
	 * within the session context
	 */
    String CONVERSATION_CONTEXT_MANAGER_KEY = "rees.scope.conversation.context.manager.key";
    
    /**
	 * used as a key to identify the TimeoutMonitor 
	 * within the session context
	 */
    String CONVERSATION_TIMEOUT_MONITOR_KEY = "rees.scope.conversation.timeout.monitor.key";
    
    String CONVERSATION_NAME_SUFFIX = "_conversation";
    String DEFAULT_CONTROLLER_SUFFIX = "Controller";

    String CONVERSATION_PREPARE_REDIRECT_RESULT = "conversationPrepareRedirect";
    
    interface Properties {

		String CONVERSATION_MONITORING_FREQUENCY = "conversation.monitoring.frequency";
		String CONVERSATION_MONITORING_THREAD_POOL_SIZE = "conversation.monitoring.thread.pool.size";
		String CONVERSATION_IDLE_TIMEOUT = "conversation.idle.timeout";
		String CONVERSATION_MAX_INSTANCES = "conversation.max.instances";
		String CONVERSATION_PACKAGE_NESTING_KEY = "conversation.package.nesting";
        String CONVERSATION_AUTO_START = "conversation.auto.start";
    	
    }
    
}
