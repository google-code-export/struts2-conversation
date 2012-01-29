package com.google.code.rees.scope.struts2;

import java.util.HashMap;
import java.util.Map;

import com.google.code.rees.scope.session.SessionAdapter;
import com.google.code.rees.scope.session.SessionPostProcessor;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.PreResultListener;

public class StrutsSessionAdapter extends SessionAdapter {

	private static final long serialVersionUID = -5521071699714160473L;
	
	protected ActionInvocation invocation;
	protected ActionContext actionContext;
	protected Map<String, Object> sessionContext;

	@SuppressWarnings("unchecked")
	public StrutsSessionAdapter(ActionInvocation invocation) {
		this.invocation = invocation;
		this.actionContext = invocation.getInvocationContext();
		sessionContext = (Map<String, Object>) actionContext.getSession().get(StrutsScopeConstants.SESSION_FIELD_MAP_KEY);
        if (sessionContext == null) {
        	sessionContext = new HashMap<String, Object>();
        	actionContext.getSession().put(StrutsScopeConstants.SESSION_FIELD_MAP_KEY, sessionContext);
        }
	}

	@Override
	public Object getAction() {
		return this.invocation.getAction();
	}

	@Override
	public String getActionId() {
		return this.invocation.getProxy().getMethod();
	}

	@Override
    public Map<String, Object> getSessionContext() {
    	return this.sessionContext;
    }

	@Override
	public void dispatchPostProcessor(SessionPostProcessor sessionPostProcessor) {
		this.invocation.addPreResultListener(new SessionResultListener(this,
				sessionPostProcessor));
	}

	class SessionResultListener implements PreResultListener {

		private SessionPostProcessor postProcessor;
		private SessionAdapter sessionAdapter;

		SessionResultListener(SessionAdapter sessionAdapter,
				SessionPostProcessor postProcessor) {
			this.sessionAdapter = sessionAdapter;
			this.postProcessor = postProcessor;
		}

		@Override
		public void beforeResult(ActionInvocation invocation, String resultCode) {
			this.postProcessor.postProcessSession(sessionAdapter);
		}
	}

}
