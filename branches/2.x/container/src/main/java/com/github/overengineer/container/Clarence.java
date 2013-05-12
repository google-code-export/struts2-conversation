package com.github.overengineer.container;

import com.github.overengineer.container.factory.DefaultMetaFactory;
import com.github.overengineer.container.factory.MetaFactory;
import com.github.overengineer.container.inject.DefaultInjectorFactory;
import com.github.overengineer.container.inject.InjectorFactory;
import com.github.overengineer.container.instantiate.*;
import com.github.overengineer.container.key.DefaultKeyRepository;
import com.github.overengineer.container.key.KeyRepository;
import com.github.overengineer.container.metadata.DefaultMetadataAdapter;
import com.github.overengineer.container.metadata.MetadataAdapter;
import com.github.overengineer.container.proxy.HotSwappableContainer;
import com.github.overengineer.container.proxy.ProxyModule;
import com.github.overengineer.container.proxy.aop.AopContainer;
import com.github.overengineer.container.proxy.aop.AopModule;

/**
 * @author rees.byars
 */
public class Clarence {

    private Container builder;

    {
        MetadataAdapter metadataAdapter = new DefaultMetadataAdapter();
        InjectorFactory injectorFactory = new DefaultInjectorFactory(metadataAdapter);
        KeyRepository keyRepository = new DefaultKeyRepository();
        ParameterProxyFactory parameterProxyFactory = new DefaultParameterProxyFactory(metadataAdapter, keyRepository);
        ConstructorResolver constructorResolver = new DefaultConstructorResolver();
        InstantiatorFactory instantiatorFactory = new DefaultInstantiatorFactory(constructorResolver, parameterProxyFactory);
        ComponentStrategyFactory strategyFactory = new DefaultComponentStrategyFactory(injectorFactory, instantiatorFactory);

        MetaFactory metaFactory = new DefaultMetaFactory();
        builder = new DefaultContainer(strategyFactory, keyRepository, metaFactory);
        builder.addInstance(MetadataAdapter.class, metadataAdapter);
        builder.addInstance(InjectorFactory.class, injectorFactory);
        builder.addInstance(ParameterProxyFactory.class, parameterProxyFactory);
        builder.addInstance(ConstructorResolver.class, constructorResolver);
        builder.addInstance(InstantiatorFactory.class, instantiatorFactory);
        builder.addInstance(KeyRepository.class, keyRepository);
        builder.addInstance(MetaFactory.class, metaFactory);
        builder.addInstance(ComponentStrategyFactory.class, strategyFactory);
    }

    public static Clarence please() {
        return new Clarence();
    }

    public HotSwappableContainer gimmeThatProxyTainer() {
        return (HotSwappableContainer) builder.loadModule(ProxyModule.class).get(HotSwappableContainer.class).addChild(builder);
    }

    public AopContainer gimmeThatAopTainer() {
        return (AopContainer) builder.loadModule(AopModule.class).get(AopContainer.class).addChild(builder);
    }

    public Container gimmeThatTainer() {
        return builder;
    }

}
