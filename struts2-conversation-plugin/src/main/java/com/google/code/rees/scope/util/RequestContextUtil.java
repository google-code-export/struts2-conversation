package com.google.code.rees.scope.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import com.google.code.rees.scope.conversation.ConversationConstants;

public class RequestContextUtil {

    public static Map<String, String> getRequestContext(
            HttpServletRequest request) {

        Map<String, String> requestContext = new HashMap<String, String>();
        if (request != null) {
            Map<String, String[]> params = request.getParameterMap();
            for (Entry<String, String[]> param : params.entrySet()) {
                if (param
                        .getKey()
                        .endsWith(
                                ConversationConstants.CONVERSATION_NAME_SESSION_MAP_SUFFIX)) {
                    requestContext.put(param.getKey(), param.getValue()[0]);
                }
            }
        }

        return requestContext;
    }

}
