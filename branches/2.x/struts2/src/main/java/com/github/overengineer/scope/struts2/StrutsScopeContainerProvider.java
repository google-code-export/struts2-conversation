package com.github.overengineer.scope.struts2;

import com.github.overengineer.scope.container.ScopeContainer;
import com.github.overengineer.scope.container.ScopeContainerProvider;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.Inject;

public class StrutsScopeContainerProvider implements ScopeContainerProvider {
	
	private static final long serialVersionUID = 5288295007865319291L;
	
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

	@Override
	public ScopeContainer getScopeContainer() {
		return container.getInstance(ScopeContainer.class, scopeContainerKey);
	}

}
