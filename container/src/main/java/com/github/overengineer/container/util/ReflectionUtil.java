package com.github.overengineer.container.util;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

/**
 */
public class ReflectionUtil {

    public static boolean isPublicSetter(Method method) {
        return method.getName().startsWith("set") && method.getParameterTypes().length == 1 && method.getReturnType() == Void.TYPE;
    }

    public static boolean isPropertyType(Class cls) {
        return
                cls.isPrimitive() ||
                String.class.isAssignableFrom(cls) ||
                Number.class.isAssignableFrom(cls) ||
                Boolean.class.isAssignableFrom(cls);
    }

    public static Set<Class<?>> getAllInterfaces(Class<?> cls) {
        Set<Class<?>> interfaces = new HashSet<Class<?>>();
        getAllInterfaces(cls, interfaces);
        return interfaces;
    }

    public static void getAllInterfaces(Class<?> cls, Set<Class<?>> interfacesFound) {
        while (cls != null) {
            Class<?>[] interfaces = cls.getInterfaces();
            for (Class<?> i : interfaces) {
                if (interfacesFound.add(i)) {
                    getAllInterfaces(i, interfacesFound);
                }
            }
            cls = cls.getSuperclass();
        }
    }

}
