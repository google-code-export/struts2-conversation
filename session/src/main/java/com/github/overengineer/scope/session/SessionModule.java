package com.github.overengineer.scope.session;

import com.github.overengineer.container.BaseModule;

public class SessionModule extends BaseModule {

    public SessionModule() {

        use(DefaultSessionConfigurationProvider.class).forType(SessionConfigurationProvider.class);
        use(DefaultSessionManager.class).forType(SessionManager.class);

    }

}
