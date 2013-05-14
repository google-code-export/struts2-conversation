package com.github.overengineer.container;

import com.github.overengineer.container.factory.DefaultMetaFactory;
import com.github.overengineer.container.factory.MetaFactory;
import com.github.overengineer.container.inject.DefaultInjectorFactory;
import com.github.overengineer.container.inject.InjectorFactory;
import com.github.overengineer.container.instantiate.*;
import com.github.overengineer.container.key.DefaultKeyRepository;
import com.github.overengineer.container.key.GenericKey;
import com.github.overengineer.container.key.KeyRepository;
import com.github.overengineer.container.metadata.DefaultMetadataAdapter;
import com.github.overengineer.container.metadata.MetadataAdapter;
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
    private InjectorFactory injectorFactory = new DefaultInjectorFactory(metadataAdapter, keyRepository);
    private ParameterProxyFactory parameterProxyFactory = new DefaultParameterProxyFactory(metadataAdapter, keyRepository);
    private ConstructorResolver constructorResolver = new DefaultConstructorResolver();
    private InstantiatorFactory instantiatorFactory = new DefaultInstantiatorFactory(constructorResolver, parameterProxyFactory);
    private List<ComponentInitializationListener> initializationListeners = new ArrayList<ComponentInitializationListener>();
    private ComponentStrategyFactory strategyFactory = new DefaultComponentStrategyFactory(metadataAdapter, injectorFactory, instantiatorFactory, initializationListeners);
    private MetaFactory metaFactory = new DefaultMetaFactory(instantiatorFactory);
    private Container builder;

    {
        builder = new DefaultContainer(strategyFactory, keyRepository, metaFactory);
        builder.addInstance(new GenericKey<List<ComponentInitializationListener>>(){}, initializationListeners);
    }

    public static Clarence please() {
        return new Clarence();
    }

    public HotSwappableContainer gimmeThatProxyTainer() {
        makeYourStuffInjectable();
        return (HotSwappableContainer) builder.loadModule(ProxyModule.class).get(HotSwappableContainer.class).addCascadingContainer(builder);
    }

    public AopContainer gimmeThatAopTainer() {
        makeYourStuffInjectable();
        return (AopContainer) builder.loadModule(AopModule.class).get(AopContainer.class).addCascadingContainer(builder);
    }

    public Container gimmeThatTainer() {
        return builder;
    }

    public Clarence makeYourStuffInjectable() {
        builder
                .addInstance(MetadataAdapter.class, metadataAdapter)
                .addInstance(InjectorFactory.class, injectorFactory)
                .addInstance(ParameterProxyFactory.class, parameterProxyFactory)
                .addInstance(ConstructorResolver.class, constructorResolver)
                .addInstance(InstantiatorFactory.class, instantiatorFactory)
                .addInstance(KeyRepository.class, keyRepository)
                .addInstance(MetaFactory.class, metaFactory)
                .addInstance(ComponentStrategyFactory.class, strategyFactory);
        return this;
    }

}
