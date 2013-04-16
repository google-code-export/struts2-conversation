package com.github.overengineer.scope.container;

import com.github.overengineer.scope.container.proxy.*;
import com.github.overengineer.scope.container.proxy.aop.*;

import java.util.ArrayList;
import java.util.List;

/**
 */
public class ContainerBuilder {

    public static Builder begin() {
        return new BasicBuilder();
    }

    public interface Builder {
        Builder withListener(Class<? extends ComponentInitializationListener> listener);
        ProxyBuilder withJdkProxies();
        Container build();
    }

    public interface ProxyBuilder {
        ProxyBuilder withInterceptor(AdvisingInterceptor interceptor);
        HotSwappableContainer build();
    }

    public static class BasicBuilder implements Builder {

        List<Class<? extends ComponentInitializationListener>> initializationListeners = new ArrayList<Class<? extends ComponentInitializationListener>>();
        BootstrapContainer bootstrapContainer = new BootstrapContainer();

        public Builder withListener(Class<? extends ComponentInitializationListener> listener) {
            initializationListeners.add(listener);
            return this;
        }

        public ProxyBuilder withJdkProxies() {
            return new ProxyBuilderImpl(this);
        }

        public Container build() {
            bootstrapContainer.properties.put(Properties.LISTENERS, initializationListeners);
            bootstrapContainer.addMapping(ComponentStrategyFactory.class, DefaultComponentStrategyFactory.class);
            bootstrapContainer.addMapping(Container.class, DefaultContainer.class);
            return bootstrapContainer.get(Container.class).get(Container.class);
        }
    }

    public static class ProxyBuilderImpl implements ProxyBuilder {

        List<AdvisingInterceptor> interceptors = new ArrayList<AdvisingInterceptor>();
        BasicBuilder basicBuilder;

        ProxyBuilderImpl(BasicBuilder basicBuilder) {
            this.basicBuilder = basicBuilder;
        }

        @Override
        public ProxyBuilder withInterceptor(AdvisingInterceptor interceptor) {
            interceptors.add(interceptor);
            return this;
        }

        @Override
        public HotSwappableContainer build() {
            basicBuilder.bootstrapContainer.addMapping(ProxyHandlerFactory.class, JdkProxyHandlerFactory.class);
            basicBuilder.bootstrapContainer.addMapping(ComponentStrategyFactory.class, DefaultComponentStrategyFactory.class);
            basicBuilder.bootstrapContainer.addMapping(ComponentStrategyFactory.class, ProxyComponentStrategyFactory.class);
            basicBuilder.bootstrapContainer.addMapping(Container.class, DefaultHotSwappableContainer.class);
            basicBuilder.bootstrapContainer.addMapping(HotSwappableContainer.class, DefaultHotSwappableContainer.class);
            basicBuilder.bootstrapContainer.properties.put(Properties.LISTENERS, basicBuilder.initializationListeners);
            if (!interceptors.isEmpty()) {
                basicBuilder.bootstrapContainer.addMapping(ProxyHandlerFactory.class, JdkAopProxyHandlerFactory.class);
                basicBuilder.bootstrapContainer.addMapping(JoinPointInvocationFactory.class, AdvisedInvocationFactory.class);
                basicBuilder.bootstrapContainer.addMapping(PointcutInterpreter.class, DefaultPointcutInterpreter.class);
                basicBuilder.bootstrapContainer.properties.put(Properties.INTERCEPTORS, interceptors);
            }
            return basicBuilder.bootstrapContainer.get(HotSwappableContainer.class).get(HotSwappableContainer.class);
        }

    }

}
