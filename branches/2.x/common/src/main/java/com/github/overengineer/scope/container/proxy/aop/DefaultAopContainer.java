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
        Aspect aspect = strategy.get(this);
        aspects.add(strategy.get(this));
        for (Container container : getChildren()) {
            if (container instanceof AopContainer) {
                ((AopContainer) container).getAspects().add(aspect);
            }
        }
        return get(AopContainer.class);
    }

    @Override
    public List<Aspect> getAspects() {
        return aspects;
    }

    @Override
    public List<Object> getAllComponents() {
        List<Object> components = new LinkedList<Object>();
        components.addAll(super.getAllComponents());
        components.addAll(aspects);
        for (Container container : getChildren()) {
            if (container instanceof AopContainer) {
                components.addAll(((AopContainer) container).getAspects());
            }
        }
        return components;
    }

}
