package com.github.overengineer.scope.struts2;

import com.github.overengineer.scope.ActionProvider;
import com.github.overengineer.scope.container.BaseModule;
import com.github.overengineer.scope.container.SimpleScopeContainer;
import com.github.overengineer.scope.conversation.ConversationModule;
import com.github.overengineer.scope.conversation.configuration.ConversationArbitrator;
import com.github.overengineer.scope.session.SessionModule;

public class StrutsModuleContainer extends SimpleScopeContainer {

    private static final long serialVersionUID = 3180479652636319036L;

    public StrutsModuleContainer() {
        loadModule(new SessionModule());
        loadModule(new ConversationModule());
        loadModule(new StrutsModule());
    }

    static class StrutsModule extends BaseModule {
        StrutsModule() {
            resolve(ConversationArbitrator.class).to(StrutsConversationArbitrator.class);
            resolve(ActionProvider.class).to(StrutsActionProvider.class);
        }
    }

}
