package com.github.overengineer.container.dynamic;

import com.github.overengineer.container.Provider;
import com.github.overengineer.container.SelectionAdvisor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;

/**
 * @author rees.byars
 */
public class DynamicComposite<T> implements InvocationHandler {

    private List<T> components;
    T proxy;
    private final Class<T> componentInterface;
    private final Provider provider;

    DynamicComposite(Class<T> componentInterface, final Provider provider) {
        this.componentInterface = componentInterface;
        this.provider = provider;
    }

    @Override
    public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
        String methodName = method.getName();
        if ("equals".equals(methodName)) {
            return proxy == objects[0];
        } else if ("hashCode".equals(methodName)) {
            return System.identityHashCode(proxy);
        } else if ("toString".equals(methodName)) {
            return proxy.getClass().getName() + '@' + Integer.toHexString(System.identityHashCode(this)) + "$DynamicComposite$[" + componentInterface.getName() + "]";
        }
        if (components == null) {
            components = provider.getAll(componentInterface, new SelectionAdvisor() {
                @Override
                public boolean validSelection(Class<?> candidateClass) {
                    return candidateClass != proxy.getClass();
                }
            });
        }
        for (T component : components) {
            method.invoke(component, objects);
        }
        return null;
    }

}
