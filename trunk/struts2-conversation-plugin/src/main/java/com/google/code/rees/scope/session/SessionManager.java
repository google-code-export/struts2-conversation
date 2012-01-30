package com.google.code.rees.scope.session;

import java.io.Serializable;

public interface SessionManager extends Serializable {
	public void init(SessionConfigurationProvider configurationProvider);
	public void processSessionFields(SessionAdapter sessionAdapter);
}
