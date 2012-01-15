package com.google.code.struts2.scope;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;

/**
 * A utility class that provides static methods that are used internally.
 * 
 * @author rees.byars
 */
public class ScopeUtil {
	
	private static final Logger LOG = LoggerFactory.getLogger(ScopeUtil.class);
	
	public static Map<String, Object> getFieldValues(Object action,
			Map<String, Field> classFieldMap) {
		
		Map<String, Object> scopedValuesMap = new HashMap<String, Object>();
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
				scopedValuesMap.put(fieldName, value);
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
		return scopedValuesMap;
	}

	public static void setFieldValues(Object action,
			Map<String, Field> classFieldMap,
			Map<String, Object> scopedValuesMap) {
		
		for (Entry<String,Field> fieldEntry: classFieldMap.entrySet()) {
			Field field = fieldEntry.getValue();
			String fieldName = fieldEntry.getKey();
			if (LOG.isDebugEnabled()) {
				LOG.debug("Setting field by the name of " + fieldName);
			}
			Object value = scopedValuesMap.get(fieldName);
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
