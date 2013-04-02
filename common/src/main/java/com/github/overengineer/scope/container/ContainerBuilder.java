package com.github.overengineer.scope.container;

import com.github.overengineer.scope.container.proxy.ProxyComponentStrategyFactory;

/**
 */
public class ContainerBuilder {

    public static <T extends ComponentStrategyFactory> Builder withProxies() {
        Builder builder = new Builder();
        builder.strategyFactory = new ProxyComponentStrategyFactory(new DefaultComponentStrategyFactory());
        return builder;
    }

    public static Container build() {
        return new Builder().build();
    }

    public static class Builder {

        private ComponentStrategyFactory strategyFactory = new DefaultComponentStrategyFactory();

        public Container build() {
            return new DefaultContainer(strategyFactory);
        }
    }

}
