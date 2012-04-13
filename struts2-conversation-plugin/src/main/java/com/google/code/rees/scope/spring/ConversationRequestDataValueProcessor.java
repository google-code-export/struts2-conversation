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
 *  $Id: ConversationRequestDataValueProcessor.java reesbyars $
 ******************************************************************************/
package com.google.code.rees.scope.spring;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.servlet.support.RequestDataValueProcessor;

import com.google.code.rees.scope.conversation.ConversationAdapter;

/**
 * Configured in the Spring application XML with the ID
 * requestDataValueProcessor.
 * The Spring MVC form tags then call
 * {@link #getExtraHiddenFields(HttpServletRequest)} on this class. In this way,
 * the conversation IDs are placed in the tags.
 * 
 * @author rees.byars
 */
public class ConversationRequestDataValueProcessor implements
        RequestDataValueProcessor {

    @Override
    public String processAction(HttpServletRequest paramHttpServletRequest,
            String paramString) {
        // not used
        return null;
    }

    @Override
    public String processFormFieldValue(
            HttpServletRequest paramHttpServletRequest, String paramString1,
            String paramString2, String paramString3) {
        // not used
        return null;
    }

    @Override
    public Map<String, String> getExtraHiddenFields(
            HttpServletRequest paramHttpServletRequest) {
        return ConversationAdapter.getAdapter().getViewContext();
    }

    @Override
    public String processUrl(HttpServletRequest paramHttpServletRequest,
            String paramString) {
        // not used
        return null;
    }

}
