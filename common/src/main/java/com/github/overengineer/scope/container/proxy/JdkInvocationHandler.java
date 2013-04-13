package com.github.overengineer.scope.container.proxy;

import java.lang.reflect.Method;

/**
 */
public class JdkInvocationHandler<T> extends JdkComponentProxyHandler<T> implements InvocationHandler<T> {

    private InvocationFactory invocationFactory;

    public JdkInvocationHandler(JdkProxyFactory factory) {
        super(factory);
    }

    @Override
    public void setInvocationFactory(InvocationFactory invocationFactory) {
        this.invocationFactory = invocationFactory;
    }

    @Override
    public Object invoke(Object o, Method method, Object[] parameters) throws Throwable {
        return invocationFactory.create(component, method, parameters).invoke();
    }

}
