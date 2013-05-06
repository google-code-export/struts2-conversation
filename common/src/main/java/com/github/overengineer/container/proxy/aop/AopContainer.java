package com.github.overengineer.container.proxy.aop;

import com.github.overengineer.container.proxy.HotSwappableContainer;

import java.util.List;

/**
 * @author rees.byars
 */
public interface AopContainer extends HotSwappableContainer {
    <A extends Aspect<?>> AopContainer addAspect(Class<A> aspectClass);
    List<Aspect> getAspects();
}
