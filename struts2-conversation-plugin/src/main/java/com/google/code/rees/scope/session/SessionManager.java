package com.google.code.rees.scope.session;

public interface SessionManager {
	public void setSessionFieldConfigBuilder(SessionFieldConfigBuilder sessionFieldConfigBuilder);
	public void processSessionFields(SessionAdapter sessionAdapter);
}
