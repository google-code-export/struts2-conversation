package com.google.code.rees.scope.session;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class SessionConfiguration {
	
	private Map<Class<?>, Map<String, Field>> fields;
	
	public SessionConfiguration() {
		fields = new HashMap<Class<?>, Map<String, Field>>();
	}

	public void addField(Class<?> clazz, String name, Field field) {
		Map<String, Field> classFields = fields.get(clazz);
		if (classFields == null) {
			classFields = new HashMap<String, Field>();
		}
		classFields.put(name, field);
		fields.put(clazz, classFields);
	}
	
	public Map<String, Field> getFields(Class<?> clazz) {
		return fields.get(clazz);
	}
}
