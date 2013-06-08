package com.github.overengineer.container.parameter;

import com.github.overengineer.container.Provider;

/**
 * @author rees.byars
 */
public class TrailingArgsParameterBuilder<T> implements ParameterBuilder<T> {

    private final ParameterProxy[] proxies;
    private final boolean decorator;

    public TrailingArgsParameterBuilder(ParameterProxy[] proxies, boolean decorator) {
        this.proxies = proxies;
        this.decorator = decorator;
    }

    @Override
    public boolean isDecorator() {
        return decorator;
    }

    @Override
    public Object[] buildParameters(Provider provider, Object[] trailingArgs) {
        Object[] parameters = new Object[proxies.length + trailingArgs.length];
        for (int i = 0; i < proxies.length; i++) {
            parameters[i] = proxies[i].get(provider);
        }
        if (trailingArgs.length > 0) {
            if (proxies.length > 0) {
                System.arraycopy(trailingArgs, 0, parameters, proxies.length, trailingArgs.length + proxies.length - 1);
            } else {
                parameters = trailingArgs;
            }
        }
        return parameters;
    }
}
