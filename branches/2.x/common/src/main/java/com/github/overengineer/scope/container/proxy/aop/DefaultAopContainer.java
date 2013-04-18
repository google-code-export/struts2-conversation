package com.github.overengineer.scope.container.proxy.aop;

import com.github.overengineer.scope.container.*;
import com.github.overengineer.scope.container.proxy.DefaultHotSwappableContainer;

import java.util.LinkedList;
import java.util.List;

/**
 * @author rees.byars
 */
public class DefaultAopContainer extends DefaultHotSwappableContainer implements AopContainer {

    protected final List<Aspect> aspects;

    public DefaultAopContainer(ComponentStrategyFactory strategyFactory, @Property(Properties.ASPECTS) List<Aspect> aspects) {
        super(strategyFactory);
        this.aspects = aspects;
        addInstance(AopContainer.class, this);
    }

    @Override
    public <A extends Aspect<?>> AopContainer addAspect(Class<A> interceptorClass) {
        ComponentStrategy<A> strategy = strategyFactory.create(interceptorClass, initializationListeners);
        aspects.add(strategy.get(this));
        return get(AopContainer.class);
    }

    @Override
    public List<Object> getAllComponents() {
        List<Object> components = new LinkedList<Object>();
        components.addAll(super.getAllComponents());
        for (Aspect aspect : aspects) {
            components.add(aspect);
        }
        return components;
    }

}
