package com.google.code.rees.scope.session;

import java.lang.reflect.Field;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.code.rees.scope.util.ReflectionUtil;

/**
 * 
 * A utility class that provides static methods that are used internally and
 * for unit testing. Use outside of these contexts should come only as
 * a last resort.
 * 
 * @author rees.byars
 * 
 */
public class SessionUtil {

    private static final Logger LOG = LoggerFactory
            .getLogger(SessionUtil.class);

    /**
     * Given the name of a session-scoped field and its class, this method
     * returns the appropriate key that is used to identify instances of the
     * field in the session-field map.
     * 
     * @param name
     * @param clazz
     * @return
     */
    public static String buildKey(String name, Class<?> clazz) {
        return clazz.getName() + "." + name;
    }

    /**
     * Given a session field's name and class, the value of the field is
     * returned from the session.
     * 
     * @param <T>
     * @param name
     * @param clazz
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T getSessionField(String name, Class<T> clazz) {
        String key = buildKey(name, clazz);
        SessionAdapter adapter = SessionAdapter.getAdapter();
        if (adapter == null)
            return null;
        Map<String, Object> sessionContext = adapter.getSessionContext();
        return (T) sessionContext.get(key);
    }

    /**
     * Given a session field's name and an instance, the value is set
     * for the field in the session.
     * 
     * @param fieldName
     * @param fieldValue
     */
    public static void setSessionField(String name, Object sessionField) {
        String key = buildKey(name, sessionField.getClass());
        SessionAdapter adapter = SessionAdapter.getAdapter();
        if (adapter != null) {
            Map<String, Object> sessionContext = adapter.getSessionContext();
            sessionContext.put(key, sessionField);
        }
    }

    /**
     * The current values of session fields annotated with {@link SessionField}
     * are extracted from the target object and placed into the session. No
     * caching
     * is performed, for use only in unit testing.
     * 
     * @param target
     */
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
                    LOG.info("Illegal Argument on session field "
                            + field.getName());
                } catch (IllegalAccessException e) {
                    LOG.info("Illegal Access on session field "
                            + field.getName());
                }
            }
        }
    }

    /**
     * The target object's session fields that are annotated with
     * {@link SessionField} are injected from the session. No caching
     * is performed, for use only in unit testing.
     * 
     * @param target
     */
    public static void injectSessionFields(Object target) {
        for (Field field : ReflectionUtil.getFields(target.getClass())) {
            if (field.isAnnotationPresent(SessionField.class)) {
                Object value = getSessionField(field.getName(), field.getType());
                ReflectionUtil.makeAccessible(field);
                try {
                    field.set(target, value);
                } catch (IllegalArgumentException e) {
                    LOG.info("Illegal Argument on session field "
                            + field.getName());
                } catch (IllegalAccessException e) {
                    LOG.info("Illegal Access on session field "
                            + field.getName());
                }
            }
        }
    }

    /**
     * Returns the name of the given field's SessionField
     * 
     * @param field
     * @return
     */
    protected static String getSessionFieldName(Field field) {
        String name = field.getAnnotation(SessionField.class).name();
        if (name.equals(SessionField.DEFAULT)) {
            name = field.getName();
        }
        return name;
    }

}
