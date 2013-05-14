package com.github.overengineer.container.proxy.aop;

import com.github.overengineer.container.*;
import com.github.overengineer.container.factory.MetaFactory;
import com.github.overengineer.container.key.GenericKey;
import com.github.overengineer.container.key.SerializableKey;
import com.github.overengineer.container.proxy.DefaultHotSwappableContainer;
import com.github.overengineer.container.key.KeyRepository;

import java.util.LinkedList;
import java.util.List;

/**
 * @author rees.byars
 */
public class DefaultAopContainer extends DefaultHotSwappableContainer implements AopContainer {

    protected final SerializableKey aspectsKey = new GenericKey<List<Aspect>>() {};

    public DefaultAopContainer(ComponentStrategyFactory strategyFactory, KeyRepository keyRepository, MetaFactory metaFactory) {
        super(strategyFactory, keyRepository, metaFactory);
        addInstance(AopContainer.class, this);
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
        return get(aspectsKey);
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

}
