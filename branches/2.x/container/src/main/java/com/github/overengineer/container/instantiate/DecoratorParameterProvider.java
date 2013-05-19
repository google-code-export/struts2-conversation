package com.github.overengineer.container.instantiate;

import com.github.overengineer.container.ComponentStrategy;
import com.github.overengineer.container.key.KeyUtil;
import com.github.overengineer.container.parameter.ParameterProxy;
import com.github.overengineer.container.parameter.ParameterProxyFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * @author rees.byars
 */
public class DecoratorParameterProvider implements ParameterProxyProvider {

    private final ParameterProxyFactory parameterProxyFactory;
    private Class<?> decoratorDelegateType;
    private ComponentStrategy<?> decoratorDelegateStrategy;
    private ParameterProxy[] parameterProxies;

    public DecoratorParameterProvider(ParameterProxyFactory parameterProxyFactory, Class<?> decoratorDelegateType, ComponentStrategy<?> decoratorDelegateStrategy) {
        this.parameterProxyFactory = parameterProxyFactory;
        this.decoratorDelegateType = decoratorDelegateType;
        this.decoratorDelegateStrategy = decoratorDelegateStrategy;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onResolution(Type[] genericParameterTypes, Annotation[][] annotations) {
        parameterProxies = new ParameterProxy[genericParameterTypes.length];
        boolean decorated = false;
        for (int i = 0; i < genericParameterTypes.length; i++) {
            Type paramType = genericParameterTypes[i];
            //TODO make work for generics!!! need to compare keys
            if (decoratorDelegateType != null && KeyUtil.getClass(paramType).isAssignableFrom(decoratorDelegateType)) {
                parameterProxies[i] = new DelegateProxy(decoratorDelegateStrategy);
                decorated = true;
            } else {
                parameterProxies[i] = parameterProxyFactory.create(paramType, annotations[i]);
            }
        }

        //to prevent memory leaks in the case of millions of hot swaps
        if (!decorated) {
            decoratorDelegateStrategy = null;
            decoratorDelegateType = null;
        }
    }

    @Override
    public ParameterProxy[] getParameterProxies() {
        return parameterProxies;
    }
}
