package com.github.overengineer.scope.container.proxy.aop;

import com.github.overengineer.scope.container.proxy.ProxyUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * @author rees.byars
 */
public class DefaultPointcutInterpreter implements PointcutInterpreter {

    @Override
    public boolean appliesToMethod(Aspect aspect, Class targetClass, Method method) {
        Class<?> aspectClass = ProxyUtil.getRealComponent(aspect).getClass();
        if (aspectClass == targetClass) {
            return false;
        }
        try {
            Pointcut rules = aspectClass.getAnnotation(Pointcut.class);
            return
                    methodNameMatches(method.getName(), rules.methodNameExpression()) &&
                            returnTypeMatches(method.getReturnType(), rules.returnType()) &&
                            classMatches(targetClass, rules.classes(), rules.classNameExpression()) &&
                            parametersMatch(method.getParameterTypes(), rules.paramterTypes()) &&
                            annotationsMatch(method, rules.annotations());
        } catch (NullPointerException e) {
            return true;
        }

    }

    private boolean methodNameMatches(String targetMethodName, String rulesMethodNameExpression) {
        return rulesMethodNameExpression.equals("") || matchesWildCardExpression(targetMethodName, rulesMethodNameExpression);
    }

    private boolean returnTypeMatches(Class targetMethodReturnType, Class<?> rulesMethodReturnType) {
        return rulesMethodReturnType == Pointcut.PlaceHolder.class || rulesMethodReturnType.isAssignableFrom(targetMethodReturnType);
    }

    private boolean classMatches(Class targetClass, Class[] rulesClasses, String rulesClassNameExpression) {

        boolean rulesClassesApply = rulesClasses != null && rulesClasses.length > 0;

        if (rulesClassesApply) {
            for (Class<?> cls : rulesClasses) {
                if (cls.isAssignableFrom(targetClass)) {
                    return true;
                }
            }
        }

        if (!rulesClassNameExpression.equals("")) {
            return matchesWildCardExpression(targetClass.getName(), rulesClassNameExpression);
        }

        return !rulesClassesApply;
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
        boolean match = true;
        if (rulesAnnotations != null && rulesAnnotations.length > 0) {
            match = false;
            for (Class<? extends Annotation> a : rulesAnnotations) {
                if (targetMethod.isAnnotationPresent(a)) {
                    return true;
                }
            }
        }
        return match;
    }

    private static final String WILD_CARD = "*";

    private static boolean matchesWildCardExpression(String targetString, String wildCardExpression) {
        boolean frontWild = wildCardExpression.startsWith(WILD_CARD);
        boolean backWild = wildCardExpression.endsWith(WILD_CARD);
        if (frontWild && backWild) {
            return targetString.contains(wildCardExpression.substring(1, wildCardExpression.length() - 1));
        } else if (frontWild) {
            return targetString.endsWith(wildCardExpression.substring(1));
        } else if (backWild) {
            return targetString.startsWith(wildCardExpression.substring(0, wildCardExpression.length() - 1));
        } else {
            return targetString.equals(wildCardExpression);
        }
    }

}
