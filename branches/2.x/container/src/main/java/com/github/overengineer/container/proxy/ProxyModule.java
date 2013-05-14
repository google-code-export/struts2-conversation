package com.github.overengineer.container.proxy;

import com.github.overengineer.container.BaseModule;
import com.github.overengineer.container.ComponentStrategyFactory;
import com.github.overengineer.container.DefaultComponentStrategyFactory;

/**
 * @author rees.byars
 */
public class ProxyModule extends BaseModule {

    @Override
    protected void configure() {
        use(JdkProxyHandlerFactory.class).forType(ProxyHandlerFactory.class);
        use(DefaultComponentStrategyFactory.class).forType(ComponentStrategyFactory.class);
        use(ProxyComponentStrategyFactory.class).forType(ComponentStrategyFactory.class);
        use(DefaultHotSwappableContainer.class).forType(HotSwappableContainer.class);
    }

}
