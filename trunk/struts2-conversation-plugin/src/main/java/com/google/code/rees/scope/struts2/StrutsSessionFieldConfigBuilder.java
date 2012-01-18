package com.google.code.rees.scope.struts2;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Set;

import com.google.code.rees.scope.sessionfield.SessionField;
import com.google.code.rees.scope.sessionfield.SessionFieldConfig;
import com.google.code.rees.scope.sessionfield.SessionFieldConfigBuilder;
import com.google.code.rees.scope.sessionfield.SessionFieldUtil;
import com.google.code.rees.scope.util.ReflectionUtil;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;

/**
 * 
 * @author rees.byars
 *
 */
public class StrutsSessionFieldConfigBuilder implements SessionFieldConfigBuilder {
	
	private static final Logger LOG = LoggerFactory.getLogger(StrutsSessionFieldConfigBuilder.class);

	private ActionFinder finder;
	private SessionFieldConfig config;
	
	@Inject(StrutsScopeConstants.ACTION_FINDER_KEY)
	public void setActionClassFinder(ActionFinder finder) {
		this.finder = finder;
	}
	
	public SessionFieldConfig getSessionFieldConfig() {
		if (config == null) {
			config = new SessionFieldConfig();
			if (finder == null) {
				LOG.error("No ActionFinder was found.  " +
						"Please make sure that a bean named " + 
						StrutsScopeConstants.ACTION_FINDER_KEY +
						" of type " + ActionFinder.class.getName() + 
						" is defined in a configuration file such as struts.xml. " +
						"No @SessionField annotations can be processed.");
			} else {
				Set<Class<?>> actionClasses = finder.getActionClasses();
				for (Class<?> clazz : actionClasses) {
					if (!clazz.isInterface() && !Modifier.isAbstract(clazz.getModifiers())
							&& !clazz.isAnnotation()) {
						LOG.info("Loading @SessionField fields from " + clazz.getName());
						addFields(clazz, config);
					}
				}
			}
		}
		return config;
	}

	protected static void addFields(Class<?> clazz, SessionFieldConfig sessionFieldConfig) {
		for (Field field : ReflectionUtil.getFields(clazz)) {
			if (field.isAnnotationPresent(SessionField.class)) {
				LOG.info("Adding @SessionField " + field.getName() + " from class " + clazz.getName() + " to the SessionFieldConfig");
				SessionField sessionField = (SessionField) field
						.getAnnotation(SessionField.class);
				String name = sessionField.name();
				if (name.equals(SessionField.DEFAULT)) {
					name = field.getName();
				}
				String key = SessionFieldUtil.buildKey(name, field.getType());
				ReflectionUtil.makeAccessible(field);
				sessionFieldConfig.addField(clazz, key, field);
			}
		}
	}
	
}
