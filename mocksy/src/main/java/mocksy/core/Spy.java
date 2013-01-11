package mocksy.core;

import mocksy.Mocksy;

import java.lang.reflect.Field;

/**
 * User: reesbyars
 * Date: 9/18/12
 * Time: 12:39 PM
 * <p/>
 * Spy
 */
public class Spy<T> {

    public static <T> Infiltrator set(T value) throws DelegationException {
        return new Infiltrator(Mocksy.real(value));
    }

    public static <T> Hijacker<T> get(Class<T> classToGet) {
        return new Hijacker<T>(classToGet);
    }

    public static class Infiltrator {
        Object value;
        Infiltrator(Object value) {
            this.value = value;
        }
        public void on(Object target) {
            Object real = Mocksy.real(target);
            for (Field field : real.getClass().getDeclaredFields()) {
                if (field.getType().isAssignableFrom(value.getClass())) {
                    field.setAccessible(true);
                    try {
                        field.set(real, value);
                        return;
                    } catch (Exception e) {
                        throw new RuntimeException("Could not set field of " + value.getClass() + ":  ", e);
                    }
                }
            }
            throw new RuntimeException("Could not set field:  ", new NoSuchFieldException(real.getClass() + " does not have a field of " + value.getClass()));
        }
    }

    public static class Hijacker<T> {
        Class<T> type;
        Hijacker(Class<T> type) {
            this.type = type;
        }
        @SuppressWarnings("unchecked")
		public T from(Object target) {
            Object real = Mocksy.real(target);
            for (Field field : real.getClass().getDeclaredFields()) {
                if (field.getType().equals(type)) {
                    field.setAccessible(true);
                    try {
                        return (T) field.get(real);
                    } catch (Exception e) {
                        throw new RuntimeException("Could not get field of " + type + ":  ", e);
                    }
                }
            }
            throw new RuntimeException("Could not get field:  ", new NoSuchFieldException(real.getClass() + " does not have a field of " + type));
        }
    }

}
