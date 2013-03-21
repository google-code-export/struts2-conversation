package com.github.overengineer.scope.session;

import com.github.overengineer.scope.container.BaseModule;

public class SessionModule extends BaseModule {
	
	public SessionModule() {
		
		resolve(SessionConfigurationProvider.class).to(DefaultSessionConfigurationProvider.class);
		resolve(SessionManager.class).to(DefaultSessionManager.class);
		
	}

}
