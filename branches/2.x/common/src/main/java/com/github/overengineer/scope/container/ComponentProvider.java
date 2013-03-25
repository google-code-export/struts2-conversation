package com.github.overengineer.scope.container;

import java.io.Serializable;

/**
 */
public interface ComponentProvider extends Serializable {

    <T> T get(Class<T> clazz);

}
