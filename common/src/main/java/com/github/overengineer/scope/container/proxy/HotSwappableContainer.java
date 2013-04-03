package com.github.overengineer.scope.container.proxy;

import com.github.overengineer.scope.container.Container;

/**
 */
public interface HotSwappableContainer extends Container {

    <T> T swap(Class<T> target, Class<? extends T> implementation) throws HotSwapException;

    <T, I extends T> T swap(Class<T> target, I implementation) throws HotSwapException;

}
