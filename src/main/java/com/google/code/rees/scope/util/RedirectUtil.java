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
 *  $Id: RedirectUtil.java reesbyars $
 ******************************************************************************/
package com.google.code.rees.scope.util;

import java.util.Map;
import java.util.Map.Entry;

/**
 * 
 * @author rees.byars
 * 
 */
public class RedirectUtil {

    public static String getUrlParamString(String url, Map<String, String> viewContext) {
        StringBuilder builder = new StringBuilder(url);
        if (viewContext.size() > 0) {
            if (url.contains("?")) {
                for (Entry<String, String> param : viewContext.entrySet()) {
                    builder.append("&").append(param.getKey()).append("=").append(param.getValue());
                }
            } else {
                builder.append("?");
                for (Entry<String, String> param : viewContext.entrySet()) {
                    builder.append(param.getKey()).append("=").append(param.getValue()).append("&");
                }
                builder = builder.delete(builder.length() - 1, builder.length());
            }
        }
        return builder.toString();
    }

}
