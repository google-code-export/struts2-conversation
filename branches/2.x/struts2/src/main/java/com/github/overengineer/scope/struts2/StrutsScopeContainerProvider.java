package com.github.overengineer.scope.struts2;

import com.github.overengineer.container.Provider;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.Inject;

public class StrutsScopeContainerProvider {

    private Container container;
    private String scopeContainerKey;

    @Inject(StrutsScopeConstants.SCOPE_CONTAINER_KEY)
    public void setScopeContainerKey(String key) {
        scopeContainerKey = key;
    }

    @Inject
    public void setContainer(Container container) {
        this.container = container;
    }

    public Provider getProvider() {
        return container.getInstance(Provider.class, scopeContainerKey);
    }

}
