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
 *  $Id: RequestContextUtil.java reesbyars $
 ******************************************************************************/
package com.github.overengineer.scope.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

/**
 * Utility for creating the RequestContexts for adapters
 *
 * @author rees.byars
 */
public class RequestContextUtil {

    public static Map<String, String> getRequestContext(HttpServletRequest request) {

        Map<String, String> requestContext = new HashMap<String, String>();
        if (request != null) {
            Map<String, String[]> params = request.getParameterMap();
            for (Entry<String, String[]> param : params.entrySet()) {
                String candidateConversationName = param.getKey();
                String candidateConversationId = param.getValue()[0];
                requestContext.put(candidateConversationName, candidateConversationId);
            }
        }

        return requestContext;
    }

}
