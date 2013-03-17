package com.github.overengineer.scope.session;

import com.github.overengineer.scope.container.AbstractModule;

public class SessionModule extends AbstractModule {
	
	public SessionModule() {
		setComponent(SessionConfigurationProvider.class, DefaultSessionConfigurationProvider.class);
		setComponent(SessionManager.class, DefaultSessionManager.class);
	}

}
