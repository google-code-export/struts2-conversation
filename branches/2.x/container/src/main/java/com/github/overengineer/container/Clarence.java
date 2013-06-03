package com.github.overengineer.container;

import com.github.overengineer.container.factory.DefaultDynamicComponentFactory;
import com.github.overengineer.container.factory.DynamicComponentFactory;
import com.github.overengineer.container.inject.DefaultInjectorFactory;
import com.github.overengineer.container.inject.InjectorFactory;
import com.github.overengineer.container.instantiate.*;
import com.github.overengineer.container.key.DefaultKeyRepository;
import com.github.overengineer.container.key.GenericKey;
import com.github.overengineer.container.key.KeyRepository;
import com.github.overengineer.container.metadata.DefaultMetadataAdapter;
import com.github.overengineer.container.metadata.MetadataAdapter;
import com.github.overengineer.container.parameter.DefaultParameterBuilderFactory;
import com.github.overengineer.container.parameter.ParameterBuilderFactory;
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
    private KeyRepository keyRepository = new DefaultKeyRepository();
    private ParameterBuilderFactory parameterBuilderFactory = new DefaultParameterBuilderFactory(metadataAdapter, keyRepository);
    private InjectorFactory injectorFactory = new DefaultInjectorFactory(metadataAdapter, parameterBuilderFactory);
    private ConstructorResolver constructorResolver = new DefaultConstructorResolver(metadataAdapter);
    private InstantiatorFactory instantiatorFactory = new DefaultInstantiatorFactory(constructorResolver, parameterBuilderFactory);
    private List<ComponentInitializationListener> initializationListeners = new ArrayList<ComponentInitializationListener>();
    private ComponentStrategyFactory strategyFactory = new DefaultComponentStrategyFactory(metadataAdapter, injectorFactory, instantiatorFactory, initializationListeners);
    private DynamicComponentFactory dynamicComponentFactory = new DefaultDynamicComponentFactory(instantiatorFactory);
    private Container builder;

    {
        builder = new DefaultContainer(strategyFactory, keyRepository, dynamicComponentFactory, initializationListeners);
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
                .addInstance(KeyRepository.class, keyRepository)
                .addInstance(DynamicComponentFactory.class, dynamicComponentFactory)
                .addInstance(ComponentStrategyFactory.class, strategyFactory)
                .addInstance(new GenericKey<List<ComponentInitializationListener>>() {
                }, initializationListeners);
        return this;
    }

}