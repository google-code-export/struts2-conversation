package com.google.code.rees.scope.sessionfield;

public interface SessionManager {
	public void setSessionFieldConfigBuilder(SessionFieldConfigBuilder sessionFieldConfigBuilder);
	public void processSessionFields(SessionAdapter sessionAdapter);
}
