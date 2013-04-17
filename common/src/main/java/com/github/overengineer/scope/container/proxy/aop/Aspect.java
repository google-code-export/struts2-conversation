package com.github.overengineer.scope.container.proxy.aop;

import java.io.Serializable;

/**
 * @author rees.byars
 */
public interface Aspect<T> extends Serializable {

    Object advise(JoinPointInvocation<T> invocation) throws Throwable;

}
