package com.google.code.rees.scope.conversation;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 
 * @author rees.byars
 * 
 */
public class DefaultConversationConfigurationProvider implements ConversationConfigurationProvider {

	private static final long serialVersionUID = -1227350994518195549L;
	
	protected ConversationArbitrator arbitrator;
	protected Map<Class<?>, Collection<ConversationConfiguration>> classConfigurations;

	@Override
	public void init() {
		classConfigurations = new HashMap<Class<?>, Collection<ConversationConfiguration>>();
	}
	
	@Override
	public void init(Set<Class<?>> actionClasses) {
		this.init();
		for (Class<?> clazz : actionClasses) {
			processClass(clazz);
		}
	}

	@Override
	public void setArbitrator(ConversationArbitrator arbitrator) {
		this.arbitrator = arbitrator;
	}

	@Override
	public Collection<ConversationConfiguration> getConfigurations(Class<?> actionClass) {
		// TODO Auto-generated method stub
		return null;
	}
	
	protected void processClass(Class<?> clazz) {
		//TODO
	}

	

}
