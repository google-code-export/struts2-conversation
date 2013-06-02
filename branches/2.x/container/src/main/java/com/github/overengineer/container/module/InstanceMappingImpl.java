package com.github.overengineer.container.module;

/**
 * @author rees.byars
 */
public class InstanceMappingImpl<T> extends TypeMapping<T> implements InstanceMapping<T> {

    private final T instance;

    @SuppressWarnings("unchecked")
    public InstanceMappingImpl(T instance) {
        super((Class<T>) instance.getClass());
        this.instance = instance;
    }

    @Override
    public T getInstance() {
        return instance;
    }
}
