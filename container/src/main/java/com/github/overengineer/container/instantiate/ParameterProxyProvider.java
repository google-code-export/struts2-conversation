package com.github.overengineer.container.instantiate;

/**
 * @author rees.byars
 */
public interface ParameterProxyProvider extends ConstructorResolver.Callback {
    ParameterProxy[] getParameterProxies();
}
