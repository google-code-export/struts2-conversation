package com.github.overengineer.scope.container.proxy;

import com.github.overengineer.scope.container.BaseModule;
import com.github.overengineer.scope.container.ComponentStrategyFactory;
import com.github.overengineer.scope.container.Container;
import com.github.overengineer.scope.container.DefaultComponentStrategyFactory;

/**
 * @author rees.byars
 */
public class ProxyModule extends BaseModule {

    public ProxyModule() {
        use(JdkProxyHandlerFactory.class).forType(ProxyHandlerFactory.class);
        use(DefaultComponentStrategyFactory.class).forType(ComponentStrategyFactory.class);
        use(ProxyComponentStrategyFactory.class).forType(ComponentStrategyFactory.class);
        use(DefaultHotSwappableContainer.class).forType(Container.class);
        use(DefaultHotSwappableContainer.class).forType(HotSwappableContainer.class);
    }

}
