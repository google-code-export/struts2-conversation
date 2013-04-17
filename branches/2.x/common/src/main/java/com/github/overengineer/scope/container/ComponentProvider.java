package com.github.overengineer.scope.container;

import java.io.Serializable;

/**
 * @author rees.byars
 */
public interface ComponentProvider extends Serializable {

    <T> T get(Class<T> clazz);

}
