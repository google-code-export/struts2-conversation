package com.github.overengineer.scope.container.proxy.aop;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 */
public class DefaultPointcutInterpreter implements PointcutInterpreter {

    @Override
    public boolean appliesToMethod(AdvisingInterceptor interceptor, Class targetClass, Method method) {
        if (method.getDeclaringClass() == AdvisingInterceptor.class) {
            return false;
        }
        Pointcut rules = interceptor.getClass().getAnnotation(Pointcut.class);
        return
                methodNameMatches(method.getName(), rules.methodNameExpression()) &&
                returnTypeMatches(method.getReturnType(), rules.returnType()) &&
                classMatches(targetClass, rules.classes(), rules.classNameExpression()) &&
                parametersMatch(method.getParameterTypes(), rules.paramterTypes()) &&
                annotationsMatch(method, rules.annotations());
    }

    private boolean methodNameMatches(String targetMethodName, String rulesMethodNameExpression) {
        return true;
    }

    private boolean returnTypeMatches(Class targetMethodReturnType, Class<?> rulesMethodReturnType) {
        return rulesMethodReturnType == Pointcut.PlaceHolder.class || rulesMethodReturnType.isAssignableFrom(targetMethodReturnType);
    }

    private boolean classMatches(Class targetClass, Class[] rulesClasses, String rulesClassNameExpression) {

        boolean classObjectMatches = true;
        if (rulesClasses != null && rulesClasses.length > 0) {
            classObjectMatches = false;
            for (Class<?> cls : rulesClasses) {
                if (cls.isAssignableFrom(targetClass)) {
                    return true;
                }
            }
        }

        boolean expressionMatches = true;
        if (rulesClassNameExpression != null) {
            expressionMatches = false;

        }

        return classObjectMatches; // || expressionMatches;
    }

    private boolean parametersMatch(Class[] targetParameterTypes, Class<?>[] rulesParameterTypes) {
        if (rulesParameterTypes.length == 1 && rulesParameterTypes[0] == Pointcut.PlaceHolder.class) {
            return true;
        }
        if (targetParameterTypes.length != rulesParameterTypes.length) {
            return false;
        }
        for (int i = 0; i < rulesParameterTypes.length; i++) {
            if (!rulesParameterTypes[i].isAssignableFrom(targetParameterTypes[i])) {
                return false;
            }
        }
        return true;
    }

    private boolean annotationsMatch(Method targetMethod, Class<? extends Annotation>[] rulesAnnotations) {
        if (rulesAnnotations.length == 1 && rulesAnnotations[0] == Pointcut.PlaceHolder.class) {
            return true;
        }
        for (Class<? extends Annotation> rulesAnnotation : rulesAnnotations) {
            if (targetMethod.isAnnotationPresent(rulesAnnotation)) {
                return true;
            }
        }
        return false;
    }

}
