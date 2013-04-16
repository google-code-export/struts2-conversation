package com.github.overengineer.scope.container.proxy.aop;

import com.github.overengineer.scope.container.*;
import com.github.overengineer.scope.container.proxy.DefaultHotSwappableContainer;

import java.util.List;

/**
 */
public class DefaultAopContainer extends DefaultHotSwappableContainer implements AopContainer {

    protected final List<AdvisingInterceptor> interceptors;
    protected ComponentStrategyFactory interceptorStrategyFactory = new DefaultComponentStrategyFactory();

    public DefaultAopContainer(ComponentStrategyFactory strategyFactory, @Property(Properties.INTERCEPTORS) List<AdvisingInterceptor> interceptors) {
        super(strategyFactory);
        this.interceptors = interceptors;
        addInstance(AopContainer.class, this);
    }


    @Override
    public <A extends AdvisingInterceptor<?>> AopContainer addInterceptor(Class<A> interceptorClass) {
        ComponentStrategy<A> strategy = interceptorStrategyFactory.create(interceptorClass, initializationListeners);
        interceptors.add(strategy.get(this));
        return get(AopContainer.class);
    }
}
