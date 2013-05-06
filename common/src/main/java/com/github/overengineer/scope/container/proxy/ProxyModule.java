package com.github.overengineer.scope.container.proxy;

import com.github.overengineer.scope.container.BaseModule;
import com.github.overengineer.scope.container.ComponentStrategyFactory;
import com.github.overengineer.scope.container.Container;
import com.github.overengineer.scope.container.DefaultComponentStrategyFactory;
import com.github.overengineer.scope.container.key.DefaultKeyGenerator;
import com.github.overengineer.scope.container.key.KeyGenerator;

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
