package com.github.overengineer.scope.struts2;

import com.github.overengineer.scope.CommonModule;
import com.github.overengineer.scope.container.standalone.SimpleScopeContainer;
import com.github.overengineer.scope.conversation.ConversationModule;
import com.github.overengineer.scope.conversation.configuration.ConversationArbitrator;
import com.github.overengineer.scope.session.SessionModule;

public class StrutsModuleContainer extends SimpleScopeContainer {

    private static final long serialVersionUID = 3180479652636319036L;

    public StrutsModuleContainer() {
        loadModule(new CommonModule());
        loadModule(new SessionModule());
        loadModule(new ConversationModule());
        add(ConversationArbitrator.class, StrutsConversationArbitrator.class);
    }

}
