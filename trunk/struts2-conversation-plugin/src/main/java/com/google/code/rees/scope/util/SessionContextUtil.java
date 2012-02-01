package com.google.code.rees.scope.util;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class SessionContextUtil {
	
	public static final String SCOPE_SESSION_CONTEXT_KEY = "rees.scope.session.context.key";

	public static Map<String, Object> getSessionContext(HttpServletRequest request) {
		HttpSession session = request.getSession();
		@SuppressWarnings("unchecked")
		Map<String, Object> sessionContext = (Map<String, Object>) session.getAttribute(SCOPE_SESSION_CONTEXT_KEY);
		if (sessionContext == null) {
			sessionContext = new HashMap<String,Object>();
			session.setAttribute(SCOPE_SESSION_CONTEXT_KEY, sessionContext);
		}
		return sessionContext;
	}
}
