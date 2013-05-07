package com.github.overengineer.container.proxy.aop;

import com.github.overengineer.container.proxy.JdkComponentProxyHandler;
import com.github.overengineer.container.proxy.JdkProxyFactory;

import java.lang.reflect.Method;

/**
 * @author rees.byars
 */
public class JdkAopProxyHandler<T> extends JdkComponentProxyHandler<T> implements AopProxyHandler<T> {

    private final JoinPointInvocationFactory invocationFactory;

    public JdkAopProxyHandler(JdkProxyFactory factory, JoinPointInvocationFactory invocationFactory) {
        super(factory);
        this.invocationFactory = invocationFactory;
    }

    @Override
    public Object invoke(Object o, Method method, Object[] parameters) throws Throwable {
        return invocationFactory.create(component, method, parameters).invoke();
    }

}
