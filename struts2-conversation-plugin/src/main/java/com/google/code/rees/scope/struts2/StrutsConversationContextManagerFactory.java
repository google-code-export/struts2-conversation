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
 *  $Id: StrutsConversationContextManagerFactory.java reesbyars $
 ******************************************************************************/
package com.google.code.rees.scope.struts2;

import com.google.code.rees.scope.conversation.context.ConversationContextFactory;
import com.google.code.rees.scope.conversation.context.DefaultHttpConversationContextManagerFactory;
import com.google.code.rees.scope.conversation.context.HttpConversationContextManagerFactory;
import com.opensymphony.xwork2.inject.Inject;

/**
 * This class basically just serves to work with X-Work's dependency injection
 * 
 * @author rees.byars
 *
 */
public class StrutsConversationContextManagerFactory extends DefaultHttpConversationContextManagerFactory implements HttpConversationContextManagerFactory {

    private static final long serialVersionUID = 2461287910903625512L;
    
    @Inject(StrutsScopeConstants.CONVERSATION_MONITORING_THREAD_POOL_SIZE)
    public void setMonitoringThreadPoolSize(String monitoringThreadPoolSizeString) {
        super.setMonitoringThreadPoolSize(Integer.parseInt(monitoringThreadPoolSizeString));
        if (this.scheduler == null) {
        	this.init();
        }
    }
    
    @Inject(StrutsScopeConstants.CONVERSATION_IDLE_TIMEOUT)
    public void setDefaultMaxIdleTime(String defaultMaxIdleTimeString) {
        super.setDefaultMaxIdleTime(Long.parseLong(defaultMaxIdleTimeString));
    }

    @Inject(StrutsScopeConstants.CONVERSATION_MONITORING_FREQUENCY)
    public void setMonitoringFrequency(String monitoringFrequency) {
        super.setMonitoringFrequency(Long.parseLong(monitoringFrequency));
    }

    @Inject(StrutsScopeConstants.CONVERSATION_MAX_INSTANCES)
    public void setMaxInstances(String maxInstances) {
        super.setMaxInstances(Integer.parseInt(maxInstances));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Inject(StrutsScopeConstants.CONVERSATION_CONTEXT_FACTORY)
    public void setConversationContextFactory(ConversationContextFactory conversationContextFactory) {
        this.conversationContextFactory = conversationContextFactory;
    }

}
