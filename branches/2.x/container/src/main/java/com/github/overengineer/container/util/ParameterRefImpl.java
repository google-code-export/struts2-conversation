package com.github.overengineer.container.util;

import java.lang.reflect.Type;

/**
 * @author rees.byars
 */
public class ParameterRefImpl implements ParameterRef {

    private final ParameterizedFunction parameterizedFunction;
    private final int parameterIndex;
    private transient volatile Type type;

    public ParameterRefImpl(ParameterizedFunction parameterizedFunction, int parameterIndex) {
        this.parameterizedFunction = parameterizedFunction;
        this.parameterIndex = parameterIndex;
    }

    @Override
    public Type getType() {
        if (type == null) {
            synchronized (this) {
                if (type == null) {
                    type = parameterizedFunction.getParameterTypes()[parameterIndex];
                }
            }
        }
        return type;
    }

}
