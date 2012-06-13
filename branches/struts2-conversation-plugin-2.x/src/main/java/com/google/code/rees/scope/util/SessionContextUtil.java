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
 *  $Id: SessionContextUtil.java reesbyars $
 ******************************************************************************/
package com.google.code.rees.scope.util;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Utility for creating session contexts for adapters
 * 
 * @author rees.byars
 * 
 */
public class SessionContextUtil {

	public static final String SCOPE_SESSION_CONTEXT_KEY = "rees.scope.session.context.key";

	public static Map<String, Object> getSessionContext(HttpServletRequest request) {

		HttpSession session = request.getSession();

		@SuppressWarnings("unchecked")
		Map<String, Object> sessionContext = (Map<String, Object>) session.getAttribute(SCOPE_SESSION_CONTEXT_KEY);
		if (sessionContext == null) {
			sessionContext = new HashMap<String, Object>();
			session.setAttribute(SCOPE_SESSION_CONTEXT_KEY, sessionContext);
		}

		return sessionContext;
	}
}
