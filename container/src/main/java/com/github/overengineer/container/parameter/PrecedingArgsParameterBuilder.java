package com.github.overengineer.container.parameter;

import com.github.overengineer.container.Provider;

/**
 * @author rees.byars
 */
public class PrecedingArgsParameterBuilder<T> implements ParameterBuilder<T> {

    private final ParameterProxy[] proxies;
    private final boolean decorator;

    public PrecedingArgsParameterBuilder(ParameterProxy[] proxies, boolean decorator) {
        this.proxies = proxies;
        this.decorator = decorator;
    }

    @Override
    public boolean isDecorator() {
        return decorator;
    }

    @Override
    public Object[] buildParameters(Provider provider, Object[] precedingArgs) {
        Object[] parameters = new Object[proxies.length + precedingArgs.length];
        for (int i = precedingArgs.length; i < precedingArgs.length + proxies.length; i++) {
            parameters[i] = proxies[i - precedingArgs.length].get(provider);
        }
        if (precedingArgs.length > 0) {
            if (proxies.length > 0) {
                System.arraycopy(precedingArgs, 0, parameters, 0, precedingArgs.length + proxies.length - 1);
            } else {
                parameters = precedingArgs;
            }
        }
        return parameters;
    }
}
