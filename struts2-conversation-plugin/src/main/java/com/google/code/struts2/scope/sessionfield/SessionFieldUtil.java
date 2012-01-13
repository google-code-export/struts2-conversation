package com.google.code.struts2.scope.sessionfield;

import java.lang.reflect.Field;
import java.util.Map;

import com.google.code.struts2.scope.util.ReflectionUtil;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;

public class SessionFieldUtil {
	
	private static final Logger LOG = LoggerFactory
		.getLogger(SessionFieldUtil.class);
	
	public static String buildKey(String name, Class<?> clazz) {
		return clazz.getName() + "." + name;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T getSessionField(String name, Class<T> clazz) {
		String key = buildKey(name, clazz);
		Map<String, Object> sessionFieldMap = (Map<String, Object>) ActionContext.getContext().getSession().get(SessionFieldConstants.SESSION_FIELD_MAP_KEY);
		if (sessionFieldMap == null) {
			return null;
		}
		return (T) sessionFieldMap.get(key);
	}
	
	public static void setSessionField(String name, Object sessionField) {
		String key = buildKey(name, sessionField.getClass());
		@SuppressWarnings("unchecked")
		Map<String, Object> sessionFieldMap = (Map<String, Object>) ActionContext.getContext().getSession().get(SessionFieldConstants.SESSION_FIELD_MAP_KEY);
		if (sessionFieldMap == null) {
			return;
		}
		sessionFieldMap.put(key, sessionField);
	}
	
	public static void extractSessionFields(Object target) {
		Class<?> clazz = target.getClass();
		for (Field field : ReflectionUtil.getFields(clazz)) {
			if (field.isAnnotationPresent(SessionField.class)) {
				String name = getSessionFieldName(field);
				ReflectionUtil.makeAccessible(field);
				try {
					Object value = field.get(target);
					if (value != null) {
						setSessionField(name, value);
					}
				} catch (IllegalArgumentException e) {
					LOG.info("Illegal Argument on conversation field " + field.getName());
				} catch (IllegalAccessException e) {
					LOG.info("Illegal Access on conversation field " + field.getName());
				}
			}
		}
	}
	
	public static void injectSessionFields(Object target) {
		for (Field field : ReflectionUtil.getFields(target.getClass())) {
			if (field.isAnnotationPresent(SessionField.class)) {
				Object value = getSessionField(field.getName(), field.getType());
				ReflectionUtil.makeAccessible(field);
				try {
					field.set(target, value);
				} catch (IllegalArgumentException e) {
					LOG.info("Illegal Argument on conversation field " + field.getName());
				} catch (IllegalAccessException e) {
					LOG.info("Illegal Access on conversation field " + field.getName());
				}
			}
		}
	}
	
	protected static String getSessionFieldName(Field field) {
		String name = field.getAnnotation(SessionField.class).name();
		if (name.equals(SessionField.DEFAULT)) {
			name = field.getName();
		}
		return name;
	}

}
