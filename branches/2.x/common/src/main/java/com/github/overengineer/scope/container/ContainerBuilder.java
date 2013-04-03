package com.github.overengineer.scope.container;

import com.github.overengineer.scope.container.proxy.CgLibProxyHandlerFactory;
import com.github.overengineer.scope.container.proxy.JdkProxyHandlerFactory;
import com.github.overengineer.scope.container.proxy.ProxyComponentStrategyFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 */
public class ContainerBuilder {

    public static Builder with(Collection<ComponentInitializationListener> listeners) {
        return new Builder().with(listeners);
    }

    public static Builder with(ComponentInitializationListener listener) {
        return new Builder().with(listener);
    }

    public static Builder withJdkProxies() {
        return new Builder().withJdkProxies();
    }

    public static Builder withCgLibProxies() {
        return new Builder().withCgLibProxies();
    }

    public static Container build() {
        return new Builder().build();
    }

    public static class Builder {

        private ComponentStrategyFactory strategyFactory = new DefaultComponentStrategyFactory();
        private List<ComponentInitializationListener> initializationListeners = new ArrayList<ComponentInitializationListener>();

        public Builder with(Collection<ComponentInitializationListener> listeners) {
            for (ComponentInitializationListener listener : listeners) {
                initializationListeners.add(listener);
            }
            return this;
        }

        public Builder with(ComponentInitializationListener listener) {
            initializationListeners.add(listener);
            return this;
        }

        public Builder withJdkProxies() {
            strategyFactory = new ProxyComponentStrategyFactory(strategyFactory, new JdkProxyHandlerFactory());
            return this;
        }

        public Builder withCgLibProxies() {
            strategyFactory = new ProxyComponentStrategyFactory(strategyFactory, new CgLibProxyHandlerFactory());
            return this;
        }

        public Container build() {
            Container container = new DefaultContainer(strategyFactory);
            for (ComponentInitializationListener listener : initializationListeners) {
                container.addListener(listener);
            }
            return container;
        }
    }

}
