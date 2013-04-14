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
public class InterceptableInvocationFactory  implements InvocationFactory {

    private List<Interceptor> interceptors;
    private InterceptorRulesInterpretor rulesInterpretor;
    private Map<InterceptorCacheKey, List<Interceptor>> cache = new HashMap<InterceptorCacheKey, List<Interceptor>>();

    public InterceptableInvocationFactory(@Property(Properties.INTERCEPTORS) List<Interceptor> interceptors, InterceptorRulesInterpretor rulesInterpretor) {
        this.interceptors = interceptors;
        this.rulesInterpretor = rulesInterpretor;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Invocation<T> create(T target, Method method, Object[] parameters) {
        InterceptorCacheKey cacheKey = new InterceptorCacheKey(target.getClass(), method);
        List<Interceptor> methodInterceptors = cache.get(cacheKey);
        if (methodInterceptors == null) {
            methodInterceptors = new ArrayList<Interceptor>();
            for (Interceptor interceptor : interceptors) {
                if (rulesInterpretor.appliesToMethod(interceptor, method)) {
                    methodInterceptors.add(interceptor);
                }
            }
            cache.put(cacheKey, methodInterceptors);
        }
        return new InterceptableInvocation<T>((methodInterceptors).iterator(), target, method, parameters);
    }

    private static class InterceptorCacheKey {

        private final Class<?> targetClass;
        private final Method method;

        public InterceptorCacheKey(Class<?> targetClass, Method method) {
            this.targetClass = targetClass;
            this.method = method;
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof InterceptorCacheKey)) {
                return false;
            }
            InterceptorCacheKey otherKey = (InterceptorCacheKey) other;
            return this.method.equals(otherKey.method) && this.targetClass.equals(otherKey.targetClass);
        }

        @Override
        public int hashCode() {
            return this.method.hashCode() * 29 + (this.targetClass != null ? this.targetClass.hashCode() : 0);
        }

    }
}
