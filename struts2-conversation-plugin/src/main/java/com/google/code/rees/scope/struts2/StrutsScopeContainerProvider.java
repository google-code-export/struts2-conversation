package com.google.code.rees.scope.struts2;

import com.google.code.rees.scope.ScopeContainer;
import com.google.code.rees.scope.ScopeContainerProvider;
import com.google.code.rees.scope.struts2.StrutsScopeConstants.TypeKeys;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.Inject;

public class StrutsScopeContainerProvider implements ScopeContainerProvider {
	
	private static final long serialVersionUID = 5288295007865319291L;
	
	private Container container;
	private String scopeContainerKey;
	
	@Inject(TypeKeys.SCOPE_CONTAINER)
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
