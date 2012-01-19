package com.google.code.rees.scope.session;

import java.util.Map;

public abstract class SessionAdapter {

	protected static ThreadLocal<SessionAdapter> sessionAdapter = new ThreadLocal<SessionAdapter>();
	
	public abstract Object getAction();
	public abstract String getActionId();
	public abstract Map<String, Object> getSessionContext();
	public abstract void dispatchPostProcessor(SessionPostProcessor sessionPostProcessor);
	
	public static void setAdapter(SessionAdapter adapter) {
		sessionAdapter.set(adapter);
	}
	
	public static SessionAdapter getAdapter() {
		return sessionAdapter.get();
	}
}
