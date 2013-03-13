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
 *  $Id: HttpConversationContextManagerProvider.java reesbyars $
 ******************************************************************************/
package com.google.code.rees.scope.conversation.context;

import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;

import com.google.code.rees.scope.container.PostConstructable;

/**
 * Used for creating {@link ConversationContextManager
 * ConversationContextManagers} that are tied to the given request (or more
 * likely, that request's session)
 * 
 * @author rees.byars
 * 
 */
public interface HttpConversationContextManagerProvider extends Serializable, PostConstructable {
	
	/**
	 * The number of threads that will be used to monitor conversation timeouts
	 * 
	 * @param monitoringThreadPoolSize
	 */
	public void setMonitoringThreadPoolSize(int monitoringThreadPoolSize);
	
	/**
	 * set the frequency in milliseconds for monitoring timeouts
	 * 
	 * @param monitoringFrequency
	 */
	public void setMonitoringFrequency(long monitoringFrequency);
	
	/**
	 * set the max number of instances of conversations
	 * 
	 * @param maxInstances
	 */
	public void setMaxInstances(int maxInstances);
	
	/**
	 * set the {@link ConversationContextFactory}
	 * @param conversationContextFactory
	 */
	public void setConversationContextFactory(ConversationContextFactory conversationContextFactory);

    /**
     * Return the {@link ConversationContextManager} for this request
     * 
     * @param request
     * @return
     */
    public ConversationContextManager getManager(HttpServletRequest request);

}
