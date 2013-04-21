/*******************************************************************************
 * 
 *  Struts2-Conversation-Plugin - An Open Source Conversation- and Flow-Scope Solution for Struts2-based Applications
 *  =================================================================================================================
 * 
 *  Copyright (C) 2012 by Rees Byars
 *  http://code.google.com/p/struts2-conversation/
 * 
 * **********************************************************************************************************************
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 *  the License. You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 *  an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 *  specific language governing permissions and limitations under the License.
 * 
 * **********************************************************************************************************************
 * 
 *  $Id: ReflectionUtil.java reesbyars $
 ******************************************************************************/
package com.google.code.rees.scope.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * A utility for obtaining inherited fields, methods, and annotations, and
 * making
 * fields accessible.
 * 
 * @author rees.byars
 */
public class ReflectionUtil {

    public static Set<Field> getFields(Class<?> clazz) {
        Set<Field> fields = new HashSet<Field>();
        fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
        Class<?> superClass = clazz.getSuperclass();
        if (superClass != null) {
            if (!superClass.equals(Object.class)) {
                fields.addAll(getFields(superClass));
            }
        }
        return fields;
    }

    public static Set<Method> getMethods(Class<?> clazz) {
        Set<Method> methods = new HashSet<Method>();
        methods.addAll(Arrays.asList(clazz.getDeclaredMethods()));
        Class<?> superClass = clazz.getSuperclass();
        if (superClass != null) {
            if (!superClass.equals(Object.class)) {
                methods.addAll(getMethods(superClass));
            }
        }
        return methods;
    }

    public static Set<Class<?>> getClasses(Class<?> clazz) {
        Set<Class<?>> classes = new HashSet<Class<?>>();
        classes.addAll(Arrays.asList(clazz.getDeclaredClasses()));
        Class<?> superClass = clazz.getSuperclass();
        if (superClass != null) {
            classes.addAll(getClasses(superClass));
        }
        return classes;
    }

    public static <T extends Annotation> Set<T> getAnnotationInstances(
            Class<?> clazz, Class<T> annotationClass) {
        Set<T> annotationInstances = new HashSet<T>();
        for (Class<?> clazzClass : clazz.getInterfaces()) {
            if (clazzClass.isAnnotationPresent(annotationClass)) {
                annotationInstances.add(clazzClass
                        .getAnnotation(annotationClass));
            }
        }
        if (clazz.isAnnotationPresent(annotationClass)) {
            annotationInstances.add(clazz.getAnnotation(annotationClass));
        }
        Class<?> superClass = clazz.getSuperclass();
        if (superClass != null) {
            annotationInstances.addAll(getAnnotationInstances(superClass,
                    annotationClass));
        }
        return annotationInstances;
    }

    public static void makeAccessible(Field field) {
        if ((Modifier.isPublic(field.getModifiers()))
                && (Modifier.isPublic(field.getDeclaringClass().getModifiers()))) {
            return;
        }
        field.setAccessible(true);
    }
    
    public static boolean isPublicSetter(Method method) {
    	return method.getName().startsWith( "set") && method.getParameterTypes().length == 1 && method.getReturnType() == Void.TYPE;
    }

    public static boolean isPropertyType(Class<?> type) {
    	return type == String.class || type.isPrimitive();
    }

}
