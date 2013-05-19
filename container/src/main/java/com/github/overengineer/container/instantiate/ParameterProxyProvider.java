package com.github.overengineer.container.instantiate;

import com.github.overengineer.container.parameter.ParameterProxy;

/**
 * @author rees.byars
 */
public interface ParameterProxyProvider extends ConstructorResolver.Callback {
    ParameterProxy[] getParameterProxies();
}
