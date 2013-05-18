package com.github.overengineer.container.proxy.aop;

import com.github.overengineer.container.*;
import com.github.overengineer.container.factory.MetaFactory;
import com.github.overengineer.container.proxy.DefaultHotSwappableContainer;
import com.github.overengineer.container.key.KeyRepository;

import java.util.LinkedList;
import java.util.List;

/**
 * @author rees.byars
 */
public class DefaultAopContainer extends DefaultHotSwappableContainer implements AopContainer {

    private final List<Aspect> aspects;

    public DefaultAopContainer(ComponentStrategyFactory strategyFactory, KeyRepository keyRepository, MetaFactory metaFactory, List<ComponentInitializationListener> componentInitializationListeners, List<Aspect> aspects) {
        super(strategyFactory, keyRepository, metaFactory, componentInitializationListeners);
        this.aspects = aspects;
    }

    @Override
    public <A extends Aspect<?>> AopContainer addAspect(Class<A> interceptorClass) {
        ComponentStrategy<A> strategy = strategyFactory.create(interceptorClass);
        Aspect aspect = strategy.get(this);
        getAspects().add(strategy.get(this));
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
        components.addAll(getAspects());
        for (Container container : getChildren()) {
            if (container instanceof AopContainer) {
                components.addAll(((AopContainer) container).getAspects());
            }
        }
        return components;
    }

    @Override
    public Container makeInjectable() {
        addInstance(AopContainer.class, this);
        return super.makeInjectable();
    }

}
