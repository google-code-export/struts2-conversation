package com.github.overengineer.scope.container.proxy.aop;

import java.lang.reflect.Method;

/**
 */
public class DefaultInterceptorRulesInterpretor implements InterceptorRulesInterpretor {

    @Override
    public boolean appliesToMethod(Interceptor interceptor, Class targetClass, Method method) {
        if (method.getDeclaringClass() == Interceptor.class) {
            return false;
        }
        InterceptorRules rules = interceptor.getClass().getAnnotation(InterceptorRules.class);
        return
                methodNameMatches(method.getName(), rules.methodNameExpression()) &&
                returnTypeMatches(method.getReturnType(), rules.returnType()) &&
                classMatches(targetClass, rules.classes(), rules.classNameExpression()) &&
                parametersMatch(method.getParameterTypes(), rules.paramterTypes());
    }

    private boolean methodNameMatches(String targetMethodName, String rulesMethodNameExpression) {
        return true;
    }

    private boolean returnTypeMatches(Class targetMethodReturnType, Class<?> rulesMethodReturnType) {
        return rulesMethodReturnType == InterceptorRules.PlaceHolder.class || rulesMethodReturnType.isAssignableFrom(targetMethodReturnType);
    }

    private boolean classMatches(Class targetClass, Class[] rulesClasses, String rulesClassNameExpression) {

        boolean classObjectMatches = true;
        if (rulesClasses != null && rulesClasses.length > 0) {
            classObjectMatches = false;
            for (Class<?> cls : rulesClasses) {
                if (cls.isAssignableFrom(targetClass)) {
                    classObjectMatches = true;
                    break;
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
        if (rulesParameterTypes.length == 1 && rulesParameterTypes[0] == InterceptorRules.PlaceHolder.class) {
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

}
