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
package com.github.overengineer.scope.conversation.configuration;

import java.io.Serializable;
import java.util.Collection;

import com.github.overengineer.scope.conversation.processing.ConversationProcessor;

/**
 * This class is used by {@link ConversationProcessor ConversationManagers} to
 * obtain the {@link ConversationClassConfiguration ConversationConfigurations} for a
 * given action/controller class
 * 
 * @author rees.byars
 * 
 */
public interface ConversationConfigurationProvider extends Serializable {

    /**
     * Get the {@link ConversationClassConfiguration ConversationConfigurations} for
     * a given class
     * 
     * @param actionClass
     * @return
     */
    public Collection<ConversationClassConfiguration> getConfigurations(Class<?> actionClass);

}
