package com.github.overengineer.container.module;

/**
 * @author rees.byars
 */
public interface InstanceMapping<T> extends Mapping<T> {
    T getInstance();
}
