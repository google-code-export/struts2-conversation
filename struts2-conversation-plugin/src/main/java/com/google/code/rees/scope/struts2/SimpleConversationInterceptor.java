/***********************************************************************************************************************
 *
 * Struts2-Conversation-Plugin - An Open Source Conversation- and Flow-Scope Solution for Struts2-based Applications
 * =================================================================================================================
 *
 * Copyright (C) 2012 by Rees Byars
 * http://code.google.com/p/struts2-conversation/
 *
 ***********************************************************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 ***********************************************************************************************************************
 *
 * $Id: SimpleConversationInterceptor.java Jun 3, 2012 10:25:36 PM reesbyars $
 *
 **********************************************************************************************************************/
package com.google.code.rees.scope.struts2;

import com.google.code.rees.scope.conversation.processing.ConversationProcessor;
import com.google.code.rees.scope.conversation.processing.SimpleConversationProcessor;
import com.opensymphony.xwork2.inject.Inject;

/**
 * This interceptor uses the {@link SimpleConversationProcessor}, so it does not perform any
 * injection of @ConversationField annotated classes, however it will still work with
 * Spring or Guice.
 * 
 * @author rees.byars
 *
 */
public class SimpleConversationInterceptor extends ConversationInterceptor {

	private static final long serialVersionUID = 4778868311224251616L;

	@Override
	@Inject(StrutsScopeConstants.SIMPLE_CONVERSATION_PROCESSOR_KEY)
    public void setConversationManager(ConversationProcessor manager) {
        this.conversationProcessor = manager;
    }

}
