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
 *  $Id: ProgrammaticModelDrivenConversationUtil.java reesbyars $
 ******************************************************************************/
package com.github.overengineer.scope.struts2.programmatic;

import java.io.Serializable;

import com.github.overengineer.scope.conversation.ConversationUtil;
import com.github.overengineer.scope.conversation.context.ConversationContext;

/**
 * Utility class that uses the {@link ConversationUtil} and
 * {@link ProgrammaticModelDrivenConversation} interface
 * to assist in programmatic conversation management. Used by the
 * {@link ProgrammaticModelDrivenConversationSupport} class.
 * 
 * In most uses, actions should inherit from the
 * ProgrammaticModelDrivenConversationSupport rather than using this class
 * directly.
 * 
 * @author rees.byars
 * 
 */
public class ProgrammaticModelDrivenConversationUtil {

    public static <T extends ProgrammaticModelDrivenConversation<?>> void begin(T controller, long maxIdleTime, int maxInstances) {
        Object model = controller.getModel();
        for (String conversationName : controller.getConversations()) {
            ConversationContext conversationContext = ConversationUtil.begin(conversationName, maxIdleTime, maxInstances);
            conversationContext.put(conversationName, model);
        }
    }

    public static <M extends Serializable, T extends ProgrammaticModelDrivenConversation<M>> void persist(T controller) {
        for (String conversationName : controller.getConversations()) {
            ConversationUtil.persist(conversationName);
        }
    }

    public static <M extends Serializable, T extends ProgrammaticModelDrivenConversation<M>> void end(T controller) {
        for (String conversationName : controller.getConversations()) {
            ConversationUtil.end(conversationName);
        }
    }

    @SuppressWarnings("unchecked")
    public static <M extends Serializable, T extends ProgrammaticModelDrivenConversation<M>> M getModel(T controller, String modelKey) {
        return (M) ConversationUtil.getField(modelKey);
    }

    public static <M extends Serializable, T extends ProgrammaticModelDrivenConversation<M>> void setModel(M model, T controller, String modelKey) {
        ConversationUtil.setField(modelKey, model);
    }

}
