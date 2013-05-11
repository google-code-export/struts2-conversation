package com.github.overengineer.container.instantiate;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * @author rees.byars
 */
public class DefaultParameterProvider implements ParameterProxyProvider {

    private final ParameterProxyFactory parameterProxyFactory;
    private ParameterProxy[] parameterProxies;

    public DefaultParameterProvider(ParameterProxyFactory parameterProxyFactory) {
        this.parameterProxyFactory = parameterProxyFactory;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onResolution(Type[] genericParameterTypes, Annotation[][] annotations) {
        parameterProxies = new ParameterProxy[genericParameterTypes.length];
        for (int i = 0; i < genericParameterTypes.length; i++) {
            parameterProxies[i] = parameterProxyFactory.create(genericParameterTypes[i], annotations[i]);
        }
    }

    @Override
    public ParameterProxy[] getParameterProxies() {
        return parameterProxies;
    }
}
