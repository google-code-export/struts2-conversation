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
 *  $Id: ConversationConfigurationProvider.java reesbyars $
 ******************************************************************************/
package com.google.code.rees.scope.conversation.configuration;

import java.io.Serializable;
import java.util.Collection;
import java.util.Set;

import com.google.code.rees.scope.conversation.processing.ConversationManager;

/**
 * This class is used by {@link ConversationManager ConversationManagers} to
 * obtain the {@link ConversationConfiguration ConversationConfigurations} for a
 * given action/controller class
 * 
 * @author rees.byars
 * 
 */
public interface ConversationConfigurationProvider extends Serializable {
	
	/**
	 * Set the default max idle time for conversations
	 * 
	 * @param maxIdleTime
	 */
	public void setDefaultMaxIdleTime(long maxIdleTimeMillis);

    /**
     * Set the {@link ConversationArbitrator} to be used for building the
     * configurations
     * 
     * @param arbitrator
     */
    public void setArbitrator(ConversationArbitrator arbitrator);

    /**
     * Initialize the configuration caches for a given set of classes
     * 
     * @param actionClasses
     */
    public void init(Set<Class<?>> actionClasses);

    /**
     * Get the {@link ConversationConfiguration ConversationConfigurations} for
     * a given class
     * 
     * @param actionClass
     * @return
     */
    public Collection<ConversationConfiguration> getConfigurations(
            Class<?> actionClass);

}
