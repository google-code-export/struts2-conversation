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
 *  $Id: ConversationRedirectResult.java reesbyars $
 ******************************************************************************/
package com.google.code.rees.scope.struts2;

import com.google.code.rees.scope.conversation.ConversationAdapter;
import com.google.code.rees.scope.util.RedirectUtil;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.Result;
import org.apache.struts2.ServletActionContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Iterator;
import java.util.Map;

/**
 * 
 * @author rees.byars
 * @author pepsifan81
 * 
 */
public class ConversationPrepareRedirectResult implements Result {

    @Override
    public void execute(ActionInvocation actionInvocation) throws Exception {

        ActionContext context = actionInvocation.getInvocationContext();

        HttpServletRequest request = (HttpServletRequest) context.get(ServletActionContext.HTTP_REQUEST);
        HttpServletResponse response = (HttpServletResponse) context.get(ServletActionContext.HTTP_RESPONSE);

        String location = createRedirectLocation(request);

        // append the conversation id to the redirect url
        String locationWithConversationIds = RedirectUtil.getUrlParamString(location, ConversationAdapter.getAdapter().getViewContext());

        String finalLocation = response.encodeRedirectURL(locationWithConversationIds);

        response.sendRedirect(finalLocation);
    }

    /**
     * Recreates the URL from the request
     */
    private static String createRedirectLocation(HttpServletRequest request) {

        StringBuffer location = request.getRequestURL();
        Map<String, String[]> paramMap = request.getParameterMap();

        if (paramMap.isEmpty()) {
            return location.toString();
        }

        StringBuilder sb = new StringBuilder();
        sb.append(location.toString());
        sb.append("?");
        for (Iterator<Map.Entry<String, String[]>> iterator = paramMap.entrySet().iterator(); iterator.hasNext();) {
            Map.Entry<String, String[]> entry = iterator.next();
            sb.append(entry.getKey()).append("=").append(entry.getValue()[0]);
            if (iterator.hasNext()) {
                sb.append("&");
            }
        }

        return sb.toString();

    }
}
