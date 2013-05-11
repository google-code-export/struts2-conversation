package com.github.overengineer.container.proxy;

import com.github.overengineer.container.BaseModule;
import com.github.overengineer.container.ComponentStrategyFactory;
import com.github.overengineer.container.Container;
import com.github.overengineer.container.DefaultComponentStrategyFactory;
import com.github.overengineer.container.inject.DefaultInjectorFactory;
import com.github.overengineer.container.inject.InjectorFactory;
import com.github.overengineer.container.key.DefaultKeyGenerator;
import com.github.overengineer.container.key.KeyGenerator;
import com.github.overengineer.container.metadata.DefaultMetadataAdapter;
import com.github.overengineer.container.metadata.MetadataAdapter;

/**
 * @author rees.byars
 */
public class ProxyModule extends BaseModule {

    @Override
    protected void configure() {
        use(DefaultMetadataAdapter.class).forType(MetadataAdapter.class);
        use(DefaultInjectorFactory.class).forType(InjectorFactory.class);
        use(JdkProxyHandlerFactory.class).forType(ProxyHandlerFactory.class);
        use(DefaultComponentStrategyFactory.class).forType(ComponentStrategyFactory.class);
        use(DefaultKeyGenerator.class).forType(KeyGenerator.class);
        use(ProxyComponentStrategyFactory.class).forType(ComponentStrategyFactory.class);
        use(DefaultHotSwappableContainer.class).forType(Container.class);
        use(DefaultHotSwappableContainer.class).forType(HotSwappableContainer.class);
    }

}
