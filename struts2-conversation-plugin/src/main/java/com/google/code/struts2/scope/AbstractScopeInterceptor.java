package com.google.code.struts2.scope;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.opensymphony.xwork2.interceptor.Interceptor;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;

/**
 * 
 * @author chll.cdr
 * 
 */
public abstract class AbstractScopeInterceptor implements Interceptor {
	
	private static final long serialVersionUID = -7446754235749293759L;

	private static final Logger LOG = LoggerFactory
			.getLogger(AbstractScopeInterceptor.class);

	@Override
	public void destroy() {
		LOG.info("Destroying the " + this.getClass().getSimpleName());
	}

	protected static Map<String, Object> getFieldValues(Object action,
			Map<String, Field> classFieldMap) {

		Map<String, Object> classSessionFieldMap = new HashMap<String, Object>();
		for (Entry<String,Field> fieldEntry: classFieldMap.entrySet()) {
			Field field = fieldEntry.getValue();
			String fieldName = fieldEntry.getKey();
			try {
				if (LOG.isDebugEnabled()) {
					LOG.debug("Getting value from session for field with name "
							+ fieldName);
				}
				Object value = field.get(action);
				if (LOG.isDebugEnabled()) {
					LOG.debug("Got value from session, toString() of this value is:  "
							+ value);
				}
				classSessionFieldMap.put(fieldName, value);
			} catch (IllegalArgumentException e) {
				LOG.warn("Illegal argument exception while trying to obtain field named "
						+ fieldName
						+ " from action of class "
						+ action.getClass());
			} catch (IllegalAccessException e) {
				LOG.warn("Illegal access exception while trying to obtain field named "
						+ fieldName
						+ " from action of class "
						+ action.getClass());
			}
		}
		return classSessionFieldMap;
	}

	protected static void setFieldValues(Object action,
			Map<String, Field> classFieldMap,
			Map<String, Object> sessionFieldMap) {

		for (Entry<String,Field> fieldEntry: classFieldMap.entrySet()) {
			Field field = fieldEntry.getValue();
			String fieldName = fieldEntry.getKey();
			if (LOG.isDebugEnabled()) {
				LOG.debug("Setting field by the name of " + fieldName);
			}
			Object value = sessionFieldMap.get(fieldName);
			if (LOG.isDebugEnabled()) {
				LOG.debug("Setting field with value from session with toString() of "
						+ value);
			}
			try {
				field.set(action, value);
			} catch (IllegalAccessException ex) {
				LOG.warn("Illegal access exception while trying to set field named "
						+ fieldName
						+ " from action of class "
						+ action.getClass());
			}
		}
	}

}
