package com.github.overengineer.scope.container;

import java.io.Serializable;

/**
 */
public interface ComponentStrategyFactory extends Serializable {

    <T> ComponentStrategy<T> create(Class<T> implementationType);

    <T> ComponentStrategy<T> create(T implementation);

}
