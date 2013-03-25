package com.github.overengineer.scope.container.standalone;

import com.github.overengineer.scope.container.Provider;

/**
 */
public class SingletonInstantiator<T> implements Instantiator<T> {

    private Instantiator<T> delegateInstantiator;
    private T singleton;

    public SingletonInstantiator(Class<T> type) {
        delegateInstantiator = new PrototypeInstantiator<T>(type);
    }

    @Override
    public Class<T> getTargetType() {
        return delegateInstantiator.getTargetType();
    }

    @Override
    public T getInstance(Provider provider) {
        if (singleton == null) {
            singleton = delegateInstantiator.getInstance(provider);
        }
        return singleton;
    }
}
