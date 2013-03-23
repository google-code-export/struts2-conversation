package com.github.overengineer.scope.struts2;

import com.github.overengineer.scope.container.BaseScopeContainer;
import com.github.overengineer.scope.container.BaseScopeContainer;
import com.github.overengineer.scope.container.ScopeContainer;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.Inject;

public class StrutsScopeContainer extends BaseScopeContainer {

    private static final long serialVersionUID = -6820777796732236492L;

    private Container container;

    @Inject
    public void setContainer(Container container) {
        this.container = container;
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

    @SuppressWarnings("unchecked")
    @Override
    protected <T> T getComponentFromPrimaryContainer(Class<T> clazz) {
        if (ScopeContainer.class.isAssignableFrom(clazz)) {
            return (T) this;
        } else {
            String typeKey = container.getInstance(String.class, clazz.getName());
            if (typeKey == null) {
                return container.getInstance(clazz);
            }
            return container.getInstance(clazz, typeKey);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    protected <T> Class<? extends T> getImplementationType(Class<T> clazz) {
        return (Class<? extends T>) getComponentFromPrimaryContainer(clazz).getClass();
    }

}
