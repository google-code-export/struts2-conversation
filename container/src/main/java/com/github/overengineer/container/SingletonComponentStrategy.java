package com.github.overengineer.container;

/**
 * @author rees.byars
 */
public class SingletonComponentStrategy<T> implements ComponentStrategy<T> {

    private volatile T component;
    private final ComponentStrategy<T> delegateStrategy;

    public SingletonComponentStrategy(ComponentStrategy<T> delegateStrategy) {
        this.delegateStrategy = delegateStrategy;
    }

    @Override
    public T get(Provider provider) {
         if (component == null) {
             synchronized (this) {
                if (component == null) {
                    component = delegateStrategy.get(provider);
                }
             }
         }
         return component;
    }

}
