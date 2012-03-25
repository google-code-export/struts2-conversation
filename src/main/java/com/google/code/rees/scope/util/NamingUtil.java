package com.google.code.rees.scope.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Utility for converting camel-case names into dash-delimited names, e.g.
 * thisTypeOfName becomes this-type-of-name.
 * 
 * @author rees.byars
 */
public class NamingUtil {

    public static String getConventionName(Class<?> clazz, String suffixToRemove) {
        String conventionName = clazz.getSimpleName();
        int suffixIndex = conventionName.lastIndexOf(suffixToRemove);
        if (suffixIndex > 0) {
            conventionName = conventionName.substring(0, suffixIndex);
        }
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
