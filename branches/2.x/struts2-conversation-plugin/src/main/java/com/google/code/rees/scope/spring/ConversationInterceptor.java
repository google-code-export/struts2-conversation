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
 *  $Id: ConversationInterceptor.java reesbyars $
 ******************************************************************************/
package com.google.code.rees.scope.spring;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.google.code.rees.scope.conversation.ConversationAdapter;
import com.google.code.rees.scope.conversation.context.ConversationContextManager;
import com.google.code.rees.scope.conversation.context.HttpConversationContextManagerProvider;
import com.google.code.rees.scope.conversation.processing.ConversationProcessor;

/**
 * This Spring MVC interceptor uses a {@link ConversationProcessor} to
 * process conversations before and after controller execution.
 * 
 * @author rees.byars
 */
public class ConversationInterceptor implements HandlerInterceptor {

    protected ConversationProcessor conversationProcessor;
    protected HttpConversationContextManagerProvider conversationContextManagerProvider;

    /**
     * Set the {@link ConversationProcessor}
     * 
     * @param conversationProcessor
     */
    public void setConversationManager(ConversationProcessor conversationProcessor) {
        this.conversationProcessor = conversationProcessor;
    }

    /**
     * Set the {@link HttpConversationContextManagerProvider}
     * 
     * @param conversationContextManagerProvider
     */
    public void setConversationContextManagerProvider(HttpConversationContextManagerProvider conversationContextManagerProvider) {
        this.conversationContextManagerProvider = conversationContextManagerProvider;
    }

    /**
     * Calls {@link ConversationAdapter#executePostActionProcessors()}
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception exception) throws Exception {
        ConversationAdapter.getAdapter().executePostActionProcessors();
    }

    /**
     * This method not used by the Interceptor
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        // do nothing
    }

    /**
     * Calls
     * {@link ConversationProcessor#processConversations(ConversationAdapter)} and
     * passes in a {@link SpringConversationAdapter}.
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        ConversationContextManager contextManager = conversationContextManagerProvider.getManager(request);
        ConversationAdapter adapter = new SpringConversationAdapter(request, (HandlerMethod) handler, contextManager);
        conversationProcessor.processConversations(adapter);
        return true;
    }

}
