package com.github.overengineer.container.proxy;

import com.github.overengineer.container.BaseModule;
import com.github.overengineer.container.ComponentStrategyFactory;
import com.github.overengineer.container.Container;
import com.github.overengineer.container.DefaultComponentStrategyFactory;
import com.github.overengineer.container.key.DefaultKeyGenerator;
import com.github.overengineer.container.key.KeyGenerator;

/**
 * @author rees.byars
 */
public class ProxyModule extends BaseModule {

    public ProxyModule() {
        use(JdkProxyHandlerFactory.class).forType(ProxyHandlerFactory.class);
        use(DefaultComponentStrategyFactory.class).forType(ComponentStrategyFactory.class);
        use(DefaultKeyGenerator.class).forType(KeyGenerator.class);
        use(ProxyComponentStrategyFactory.class).forType(ComponentStrategyFactory.class);
        use(DefaultHotSwappableContainer.class).forType(Container.class);
        use(DefaultHotSwappableContainer.class).forType(HotSwappableContainer.class);
    }

}
