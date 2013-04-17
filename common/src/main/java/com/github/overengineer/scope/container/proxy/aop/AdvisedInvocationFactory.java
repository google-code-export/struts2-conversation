package com.github.overengineer.scope.container.proxy.aop;

import com.github.overengineer.scope.container.Properties;
import com.github.overengineer.scope.container.Property;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 */
public class AdvisedInvocationFactory implements JoinPointInvocationFactory {

    private final List<Aspect> aspects;
    private final PointcutInterpreter rulesInterpretor;
    private final Map<AspectCacheKey, List<Aspect>> cache = new HashMap<AspectCacheKey, List<Aspect>>();

    public AdvisedInvocationFactory(@Property(Properties.ASPECTS) List<Aspect> aspects, PointcutInterpreter rulesInterpretor) {
        this.aspects = aspects;
        this.rulesInterpretor = rulesInterpretor;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> JoinPointInvocation<T> create(T target, Method method, Object[] parameters) {
        AspectCacheKey cacheKey = new AspectCacheKey(target.getClass(), method);
        List<Aspect> methodAspects = cache.get(cacheKey);
        if (methodAspects == null) {
            methodAspects = new ArrayList<Aspect>();
            for (Aspect aspect : aspects) {
                if (rulesInterpretor.appliesToMethod(aspect, target.getClass(), method)) {
                    methodAspects.add(aspect);
                }
            }
            cache.put(cacheKey, methodAspects);
        }
        return new AdvisedInvocation<T>((methodAspects).iterator(), target, method, parameters);
    }

    private static class AspectCacheKey {

        private final Class<?> targetClass;
        private final Method method;

        public AspectCacheKey(Class<?> targetClass, Method method) {
            this.targetClass = targetClass;
            this.method = method;
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof AspectCacheKey)) {
                return false;
            }
            AspectCacheKey otherKey = (AspectCacheKey) other;
            return this.method.equals(otherKey.method) && this.targetClass.equals(otherKey.targetClass);
        }

        @Override
        public int hashCode() {
            return this.method.hashCode() * 29 + (this.targetClass != null ? this.targetClass.hashCode() : 0);
        }

    }
}
