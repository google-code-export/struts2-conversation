package com.github.overengineer.container;

import com.github.overengineer.container.key.SerializableKey;

import java.io.Serializable;

/**
 * @author rees.byars
 */
public interface Provider extends Serializable {
    <T> T get(Class<T> clazz);
    <T> T get(Class<T> clazz, String name);
    <T> T get(SerializableKey key);
}
