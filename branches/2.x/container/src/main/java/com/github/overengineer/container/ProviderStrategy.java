package com.github.overengineer.container;

import java.lang.reflect.Method;

/**
 * @author rees.byars
 */
public class ProviderStrategy<T> implements ComponentStrategy<T> {

    private final Class providerClass;
    private final String providerMethodName;
    private Method providerMethod;

    public ProviderStrategy(Class providerClass, String providerMethodName) {
        this.providerClass = providerClass;
        this.providerMethodName = providerMethodName;
    }

    @Override
    public T get(Provider provider) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
