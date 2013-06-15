package com.github.overengineer.container;

import com.github.overengineer.container.dynamic.DefaultDynamicComponentFactory;
import com.github.overengineer.container.dynamic.DynamicComponentFactory;
import com.github.overengineer.container.inject.DefaultInjectorFactory;
import com.github.overengineer.container.inject.InjectorFactory;
import com.github.overengineer.container.instantiate.*;
import com.github.overengineer.container.key.Generic;
import com.github.overengineer.container.metadata.DefaultMetadataAdapter;
import com.github.overengineer.container.metadata.MetadataAdapter;
import com.github.overengineer.container.parameter.ParameterBuilderFactory;
import com.github.overengineer.container.parameter.PrecedingArgsParameterBuilderFactory;
import com.github.overengineer.container.proxy.HotSwappableContainer;
import com.github.overengineer.container.proxy.ProxyModule;
import com.github.overengineer.container.proxy.aop.AopContainer;
import com.github.overengineer.container.proxy.aop.AopModule;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author rees.byars
 */
public class Clarence implements Serializable {

    private MetadataAdapter metadataAdapter = new DefaultMetadataAdapter();
    private ParameterBuilderFactory parameterBuilderFactory = new PrecedingArgsParameterBuilderFactory(metadataAdapter);
    private InjectorFactory injectorFactory = new DefaultInjectorFactory(metadataAdapter, parameterBuilderFactory);
    private ConstructorResolver constructorResolver = new DefaultConstructorResolver(metadataAdapter);
    private InstantiatorFactory instantiatorFactory = new DefaultInstantiatorFactory(constructorResolver, parameterBuilderFactory);
    private List<ComponentInitializationListener> initializationListeners = new ArrayList<ComponentInitializationListener>();
    private ComponentStrategyFactory strategyFactory = new DefaultComponentStrategyFactory(metadataAdapter, injectorFactory, instantiatorFactory, initializationListeners);
    private DynamicComponentFactory dynamicComponentFactory = new DefaultDynamicComponentFactory(instantiatorFactory, injectorFactory, metadataAdapter);
    private Container builder;

    {
        builder = new DefaultContainer(strategyFactory, dynamicComponentFactory, metadataAdapter, initializationListeners);
    }

    public static Clarence please() {
        return new Clarence();
    }

    public HotSwappableContainer gimmeThatProxyTainer() {
        return (HotSwappableContainer) makeYourStuffInjectable().builder.loadModule(ProxyModule.class).get(HotSwappableContainer.class).addCascadingContainer(builder);
    }

    public AopContainer gimmeThatAopTainer() {
        return (AopContainer) makeYourStuffInjectable().builder.loadModule(AopModule.class).get(AopContainer.class).addCascadingContainer(builder);
    }

    public Container gimmeThatTainer() {
        return builder;
    }

    public Clarence makeYourStuffInjectable() {
        builder
                .makeInjectable()
                .addInstance(MetadataAdapter.class, metadataAdapter)
                .addInstance(InjectorFactory.class, injectorFactory)
                .addInstance(ParameterBuilderFactory.class, parameterBuilderFactory)
                .addInstance(ConstructorResolver.class, constructorResolver)
                .addInstance(InstantiatorFactory.class, instantiatorFactory)
                .addInstance(DynamicComponentFactory.class, dynamicComponentFactory)
                .addInstance(ComponentStrategyFactory.class, strategyFactory)
                .addInstance(new Generic<List<ComponentInitializationListener>>() {
                }, initializationListeners);
        return this;
    }

}
