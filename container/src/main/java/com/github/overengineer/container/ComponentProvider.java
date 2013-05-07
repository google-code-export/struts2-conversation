package com.github.overengineer.container;

import com.github.overengineer.container.key.Key;

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
