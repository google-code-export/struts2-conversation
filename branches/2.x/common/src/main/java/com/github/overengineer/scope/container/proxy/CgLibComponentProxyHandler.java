package com.github.overengineer.scope.container.proxy;

import net.sf.cglib.proxy.*;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

/**
 */
public class CgLibComponentProxyHandler<T> implements MethodInterceptor, ComponentProxyHandler<T> {

    private static final Map<Class<?>, CgLibFactoryWrapper> FACTORIES = new HashMap<Class<?>, CgLibFactoryWrapper>();

    private T component;
    private T proxy;

    @SuppressWarnings("unchecked")
    public CgLibComponentProxyHandler(Class<?> target) {

        CgLibFactoryWrapper factory = FACTORIES.get(target);

        if (factory != null) {
            this.proxy = (T) factory.newInstance(this);
            return;
        }

        factory = createWrapper(target, this);
        this.proxy = (T) factory.newInstance(this);
        FACTORIES.put(target, factory);

    }

    @Override
    public T getProxy() {
        return proxy;
    }

    @Override
    public void setComponent(T component) {
        this.component = component;
    }

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        return method.invoke(component, objects);
    }

    private static CgLibFactoryWrapper createWrapper(Class<?> target, MethodInterceptor interceptor) {

        if (!Modifier.isFinal(target.getModifiers())) {
            Class[] parameterTypes = {};
            for (Constructor candidateConstructor : target.getDeclaredConstructors()) {
                Class[] candidateTypes = candidateConstructor.getParameterTypes();
                if (candidateTypes.length >= parameterTypes.length) {
                    parameterTypes = candidateTypes;
                }
            }
            if (parameterTypes.length == 0) {
                return new SimpleFactoryWrapper((Factory) Enhancer.create(target, interceptor));
            } else {
                Enhancer enhancer = new Enhancer();
                enhancer.setSuperclass(target);
                enhancer.setCallback(interceptor);
                Object[] params = new Object[parameterTypes.length];
                for (int i = 0; i < params.length; i++) {
                    CgLibFactoryWrapper factoryWrapper = createWrapper(parameterTypes[i], new ParamterProxy());
                    params[i] = factoryWrapper.newInstance(new ParamterProxy());
                }
                return new ParameterizedFactoryWrapper((Factory) enhancer.create(parameterTypes, params), parameterTypes, params);
            }
        } else {
             return new SimpleFactoryWrapper((Factory) Enhancer.create(target.getSuperclass(), target.getInterfaces(), interceptor));
        }


    }

    interface CgLibFactoryWrapper {
        Object newInstance(MethodInterceptor interceptor);
    }

    static class SimpleFactoryWrapper implements CgLibFactoryWrapper {
        Factory factory;
        SimpleFactoryWrapper(Factory factory) {
            this.factory = factory;
        }
        @Override
        public Object newInstance(MethodInterceptor interceptor) {
            return factory.newInstance(interceptor);
        }
    }

    static class ParameterizedFactoryWrapper implements CgLibFactoryWrapper {
        Factory factory;
        Class[] paramTypes;
        Object[] params;
        ParameterizedFactoryWrapper(Factory factory, Class[] paramTypes, Object[] params) {
            this.factory = factory;
            this.paramTypes = paramTypes;
            this.params = params;
        }
        public Object newInstance(MethodInterceptor interceptor) {
            return factory.newInstance(paramTypes, params, new Callback[]{interceptor});
        }
    }

    static class ParamterProxy implements MethodInterceptor {

        @Override
        public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
            Class<?> declaringClass = method.getDeclaringClass();

            if (declaringClass == Object.class) {
                String methodName = method.getName();
                if ("equals".equals(methodName)) {
                    return o == objects[0];
                }
                else if ("hashCode".equals(methodName)) {
                    return System.identityHashCode(o);
                }
                else if ("toString".equals(methodName)) {
                    return o.getClass().getName() + '@' + Integer.toHexString(System.identityHashCode(o));
                }
            }
            return getMockValue(method.getReturnType());
        }

        private static Object getMockValue(Class<?> type) {
            Object value = null;
            if (type.isArray()) {
                value = Array.newInstance(type.getComponentType(), 1);
            } else if (type != void.class && type.isPrimitive()) {
                value = resolvePrimitive(type);
            }
            return value;
        }

        private static Object resolvePrimitive(Class<?> type) {
            if (type == byte.class) {
                return (byte) 1;
            } else if (type == char.class) {
                return '\1';
            } else if (type == boolean.class) {
                return true;
            } else if (type == int.class) {
                return 1;
            } else if (type == long.class) {
                return 1L;
            } else if (type == double.class) {
                return 1.0;
            } else if (type == float.class) {
                return 1.0F;
            } else {
                return (short) 1;
            }
        }

    }

}
