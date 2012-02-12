package com.google.code.rees.scope.session;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is used to cache the fields and action IDs for all
 * {@link SessionField SessionFields}.
 * 
 * @see {@link SessionConfigurationProvider}
 * @see {@link SessionManager}
 * 
 * @author rees.byars
 */
public class SessionConfiguration implements Serializable {

    private static final long serialVersionUID = 1402196072477172856L;
    private Map<Class<?>, Map<String, Field>> fields;

    public SessionConfiguration() {
        fields = new HashMap<Class<?>, Map<String, Field>>();
    }

    /**
     * Caches the given field in the configuration
     * 
     * @param clazz
     * @param name
     * @param field
     */
    public void addField(Class<?> clazz, String name, Field field) {
        Map<String, Field> classFields = fields.get(clazz);
        if (classFields == null) {
            classFields = new HashMap<String, Field>();
        }
        classFields.put(name, field);
        fields.put(clazz, classFields);
    }

    /**
     * Returns the cached fields for the given class
     * 
     * @param clazz
     * @return
     */
    public Map<String, Field> getFields(Class<?> clazz) {
        return fields.get(clazz);
    }
}
