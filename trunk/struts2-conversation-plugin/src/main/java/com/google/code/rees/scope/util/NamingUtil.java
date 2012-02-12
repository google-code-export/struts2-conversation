package com.google.code.rees.scope.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class NamingUtil {

    public static String getConventionName(Class<?> clazz, String suffixToRemove) {
        String conventionName = clazz.getSimpleName();
        conventionName = conventionName.substring(0,
                conventionName.lastIndexOf(suffixToRemove));
        conventionName = getConventionName(conventionName);
        return conventionName;
    }

    public static String getConventionName(Class<?> clazz) {
        return getConventionName(clazz.getSimpleName());
    }

    public static String getConventionName(Method method) {
        return getConventionName(method.getName());
    }

    public static String getConventionName(Field field) {
        return getConventionName(field.getName());
    }

    public static String getConventionName(String camelCaseString) {
        String conventionName = camelCaseString;
        return conventionName.replaceAll(
                String.format("%s|%s|%s", "(?<=[A-Z])(?=[A-Z][a-z])",
                        "(?<=[^A-Z])(?=[A-Z])", "(?<=[A-Za-z])(?=[^A-Za-z])"),
                "-").toLowerCase();
    }

}
