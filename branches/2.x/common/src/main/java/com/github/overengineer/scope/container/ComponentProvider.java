package com.github.overengineer.scope.container;

import com.github.overengineer.scope.container.key.Key;

import java.io.Serializable;
import java.lang.reflect.Type;

/**
 * @author rees.byars
 */
public interface ComponentProvider extends Serializable {

    <T> T get(Class<T> clazz);
    <T> T get(Type type);
    <T> T get(Key key);

}
