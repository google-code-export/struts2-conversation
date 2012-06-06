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
 *  $Id: SessionManager.java reesbyars $
 ******************************************************************************/
package com.google.code.rees.scope.session;

import java.io.Serializable;

/**
 * Uses the {@link SessionConfigurationProvider} and {@link SessionAdapter} to
 * process, manage, and inject {@link SessionField SessionFields}.
 * 
 * @author rees.byars
 */
public interface SessionManager extends Serializable {

    /**
     * Set the {@link SessionConfigurationProvider}
     * 
     * @param configurationProvider
     */
    public void setConfigurationProvider(SessionConfigurationProvider configurationProvider);

    /**
     * Process, manage, and inject the {@link SessionField SessionFields}
     * 
     * @param sessionAdapter
     */
    public void processSessionFields(SessionAdapter sessionAdapter);

}
