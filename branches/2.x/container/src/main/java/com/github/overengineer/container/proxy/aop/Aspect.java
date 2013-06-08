package com.github.overengineer.container.proxy.aop;

import java.io.Serializable;

/**
 * @author rees.byars
 */
public interface Aspect<T> extends Serializable {

    Object advise(JoinPoint<T> invocation) throws Throwable;

}
