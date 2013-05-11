package com.github.overengineer.container;

import com.github.overengineer.container.factory.DefaultMetaFactory;
import com.github.overengineer.container.factory.MetaFactory;
import com.github.overengineer.container.inject.DefaultInjectorFactory;
import com.github.overengineer.container.inject.InjectorFactory;
import com.github.overengineer.container.instantiate.DefaultParameterProxyFactory;
import com.github.overengineer.container.instantiate.ParameterProxyFactory;
import com.github.overengineer.container.key.KeyGenerator;
import com.github.overengineer.container.metadata.DefaultMetadataAdapter;
import com.github.overengineer.container.metadata.MetadataAdapter;
import com.github.overengineer.container.proxy.HotSwappableContainer;
import com.github.overengineer.container.proxy.ProxyModule;
import com.github.overengineer.container.proxy.aop.AopContainer;
import com.github.overengineer.container.proxy.aop.AopModule;
import com.github.overengineer.container.key.DefaultKeyGenerator;

/**
 * @author rees.byars
 */
public class Clarence {

    MetadataAdapter metadataAdapter = new DefaultMetadataAdapter();
    InjectorFactory injectorFactory = new DefaultInjectorFactory(metadataAdapter);
    ParameterProxyFactory parameterProxyFactory = new DefaultParameterProxyFactory(metadataAdapter);
    ComponentStrategyFactory strategyFactory = new DefaultComponentStrategyFactory(injectorFactory, parameterProxyFactory);
    KeyGenerator keyGenerator = new DefaultKeyGenerator();
    MetaFactory metaFactory = new DefaultMetaFactory();
    Container builder = new DefaultContainer(strategyFactory, keyGenerator, metaFactory);

    {
        builder.addInstance(MetadataAdapter.class, metadataAdapter);
        builder.addInstance(InjectorFactory.class, injectorFactory);
        builder.addInstance(ParameterProxyFactory.class, parameterProxyFactory);
    }

    public static Clarence please() {
        return new Clarence();
    }

    public HotSwappableContainer gimmeThatProxyTainer() {
        return builder.loadModule(ProxyModule.class).get(HotSwappableContainer.class);
    }

    public AopContainer gimmeThatAopTainer() {
        return builder.loadModule(AopModule.class).get(AopContainer.class);
    }

    public Container gimmeThatTainer() {
        return builder;
    }

}
