package com.github.overengineer.scope.container.proxy.aop;

import com.github.overengineer.scope.container.proxy.HotSwappableContainer;

/**
 */
public interface AopContainer extends HotSwappableContainer {
    <A extends Aspect<?>> AopContainer addAspect(Class<A> aspectClass);
}
