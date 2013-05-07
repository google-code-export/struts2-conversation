package com.github.overengineer.container.proxy;

import com.github.overengineer.container.ComponentStrategy;
import com.github.overengineer.container.Provider;

/**
 * @author rees.byars
 */
public interface HotSwappableProxyStrategy<T> extends ComponentStrategy<T> {

    ComponentProxyHandler<T> getProxyHandler();

    public void swap(ComponentProxyHandler<T> proxyHandler, Provider provider);

}
