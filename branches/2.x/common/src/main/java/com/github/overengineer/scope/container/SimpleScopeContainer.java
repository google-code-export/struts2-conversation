package com.github.overengineer.scope.container;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.github.overengineer.scope.ActionProvider;
import com.github.overengineer.scope.CommonConstants.Defaults;
import com.github.overengineer.scope.CommonConstants.Properties;
import com.github.overengineer.scope.monitor.DefaultSchedulerProvider;
import com.github.overengineer.scope.monitor.ScheduledExecutorTimeoutMonitor;
import com.github.overengineer.scope.monitor.SchedulerProvider;
import com.github.overengineer.scope.monitor.TimeoutMonitor;

public class SimpleScopeContainer extends AbstractScopeContainer {
	
	private static final long serialVersionUID = -3502345525425524764L;
	
	private Map<Class<?>, Object> components = new HashMap<Class<?>, Object>();
	private Map<String, Object> properties = new HashMap<String, Object>();
	
	public SimpleScopeContainer() {
		components.put(ScopeContainer.class, this);
		setComponent(ActionProvider.class, EmptyActionProvider.class);
		setComponent(TimeoutMonitor.class, ScheduledExecutorTimeoutMonitor.class);
		setComponent(SchedulerProvider.class, DefaultSchedulerProvider.class);
		setProperty(Properties.MONITORING_FREQUENCY, Defaults.MONITORING_FREQUENCY);
		setProperty(Properties.MONITORING_THREAD_POOL_SIZE, Defaults.MONITORING_THREAD_POOL_SIZE);
	}
	
	protected void loadModule(Module module) {
		for (Entry<Class<?>, Class<?>> componentEntry : module.getComponentMappings().entrySet()) {
			try {
				components.put(componentEntry.getKey(), componentEntry.getValue().newInstance());
			} catch (Exception e) {
				throw new RuntimeException("There was an error attempting to create a component", e);
			}
		}
		this.setProperties(module.getProperties());
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getProperty(Class<T> clazz, String name) {
		return (T) properties.get(name);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected <T> T getComponentFromPrimaryContainer(Class<T> clazz) {
		return (T) components.get(clazz);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected <T> Class<? extends T> getImplementationType(Class<T> clazz) {
		return (Class<? extends T>) components.get(clazz).getClass();
	}
	
	public <T> void setComponents(Map<Class<T>, Class<? extends T>> components) {
		for (Entry<Class<T>, Class<? extends T>> componentEntry : components.entrySet()) {
			setComponent(componentEntry.getKey(), componentEntry.getValue());
		}
	}
	
	public <T> void setComponent(Class<T> componentType, Class<? extends T> implementationType) {
		try {
			components.put(componentType, implementationType.newInstance());
		} catch (Exception e) {
			throw new RuntimeException("There was an error attempting to create a component", e);
		}
	}
	
	public <T> void setProperties(Map<String, Object> properties) {
		for (Entry<String, Object> propertyEntry : properties.entrySet()) {
			setProperty(propertyEntry.getKey(), propertyEntry.getValue());
		}
	}
	
	public <T> void setProperty(String name, Object value) {
		properties.put(name, value);
	}
	
	public static class EmptyActionProvider implements ActionProvider {
		
		private static final long serialVersionUID = 1L;

		@Override
		public Set<Class<?>> getActionClasses() {
			return Collections.emptySet();
		}
		
	}

}
