package com.github.overengineer.container.proxy.aop;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author rees.byars
 */
public class AdvisedInvocationFactory implements JoinPointInvocationFactory {

    private final List<Aspect> aspects;
    private final PointcutInterpreter rulesInterpretor;
    private transient Map<AspectCacheKey, List<Aspect>> cache = new HashMap<AspectCacheKey, List<Aspect>>();

    public AdvisedInvocationFactory(List<Aspect> aspects, PointcutInterpreter rulesInterpretor) {
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
        return new AdvisedInvocation<T>(methodAspects.iterator(), target, method, parameters);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        cache = new HashMap<AspectCacheKey, List<Aspect>>();
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
            return method.equals(otherKey.method) && targetClass.equals(otherKey.targetClass);
        }

        @Override
        public int hashCode() {
            return method.hashCode() * 29 + (targetClass != null ? targetClass.hashCode() : 0);
        }

    }
}
