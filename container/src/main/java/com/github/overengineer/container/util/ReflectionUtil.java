package com.github.overengineer.container.util;

import java.lang.reflect.Method;

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

}
