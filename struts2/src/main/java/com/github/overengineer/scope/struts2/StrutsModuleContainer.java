package com.github.overengineer.scope.struts2;

import com.github.overengineer.scope.ActionProvider;
import com.github.overengineer.scope.CommonModule;
import com.github.overengineer.container.DefaultComponentStrategyFactory;
import com.github.overengineer.container.DefaultContainer;
import com.github.overengineer.container.key.DefaultKeyGenerator;
import com.github.overengineer.scope.conversation.ConversationModule;
import com.github.overengineer.scope.conversation.configuration.ConversationArbitrator;
import com.github.overengineer.scope.session.SessionModule;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.Inject;

public class StrutsModuleContainer extends DefaultContainer {

    private static final long serialVersionUID = 3180479652636319036L;

    private Container container;

    public StrutsModuleContainer() {
        super(new DefaultComponentStrategyFactory(), new DefaultKeyGenerator());
    }

    @Inject
    public void setContainer(Container container) {
        this.container = container;
        init();
    }

    protected void init() {
        loadModule(CommonModule.class);
        loadModule(SessionModule.class);
        loadModule(ConversationModule.class);
        add(ConversationArbitrator.class, StrutsConversationArbitrator.class);
        addStrutsInstance(ActionProvider.class);
    }

    protected <T> void addStrutsInstance(Class<T> clazz) {
        addInstance(clazz, container.getInstance(clazz, container.getInstance(String.class, clazz.getName())));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getProperty(Class<T> clazz, String name) {
        String string = container.getInstance(String.class, name);
        if (clazz == long.class) {
            return (T) Long.valueOf(string);
        } else if (clazz == int.class) {
            return (T) Integer.valueOf(string);
        } else if (clazz == boolean.class) {
            return (T) (Boolean) "true".equals(string);
        }
        return (T) string;
    }

}
