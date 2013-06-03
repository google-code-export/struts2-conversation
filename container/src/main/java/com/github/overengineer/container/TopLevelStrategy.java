package com.github.overengineer.container;

/**
 * @author rees.byars
 */
public class TopLevelStrategy<T> implements ComponentStrategy<T> {

    private final ComponentStrategy<T> delegateStrategy;

    public TopLevelStrategy(ComponentStrategy<T> delegateStrategy) {
        this.delegateStrategy = delegateStrategy;
    }

    @Override
    public T get(Provider provider) {
        return delegateStrategy.get(provider);
    }

    @Override
    public Class getComponentType() {
        return delegateStrategy.getComponentType();
    }

    @Override
    public boolean isDecorator() {
        return delegateStrategy.isDecorator();
    }
}
