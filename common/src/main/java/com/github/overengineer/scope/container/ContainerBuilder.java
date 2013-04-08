package com.github.overengineer.scope.container;

import com.github.overengineer.scope.container.proxy.HotSwappableProxyContainer;
import com.github.overengineer.scope.container.proxy.JdkProxyHandlerFactory;
import com.github.overengineer.scope.container.proxy.ProxyComponentStrategyFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 */
public class ContainerBuilder {

    public static Builder begin() {
        return new BasicBuilder();
    }

    public interface Builder {
        Builder with(Collection<ComponentInitializationListener> listeners);
        Builder with(ComponentInitializationListener listener);
        ProxyBuilder withJdkProxies();
        Container build();
    }

    public interface ProxyBuilder extends Builder {
        HotSwappableContainer build();
    }

    public static class BasicBuilder implements Builder {

        ComponentStrategyFactory strategyFactory = new DefaultComponentStrategyFactory();
        List<ComponentInitializationListener> initializationListeners = new ArrayList<ComponentInitializationListener>();

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

        public ProxyBuilder withJdkProxies() {
            return new ProxyBuilderImpl(this, new ProxyComponentStrategyFactory(strategyFactory, new JdkProxyHandlerFactory()));
        }

        public Container build() {
            Container container = new DefaultContainer(strategyFactory);
            for (ComponentInitializationListener listener : initializationListeners) {
                container.addListener(listener);
            }
            return container;
        }
    }

    public static class ProxyBuilderImpl extends BasicBuilder implements ProxyBuilder {

        ProxyComponentStrategyFactory strategyFactory;

        ProxyBuilderImpl(BasicBuilder basicBuilder, ProxyComponentStrategyFactory strategyFactory) {
            this.initializationListeners = basicBuilder.initializationListeners;
            this.strategyFactory = strategyFactory;
        }

        @Override
        public HotSwappableContainer build() {
            HotSwappableContainer container = new HotSwappableProxyContainer(strategyFactory);
            for (ComponentInitializationListener listener : initializationListeners) {
                container.addListener(listener);
            }
            return container;
        }

    }

}
