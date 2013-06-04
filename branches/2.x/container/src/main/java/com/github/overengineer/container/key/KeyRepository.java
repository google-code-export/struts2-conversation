package com.github.overengineer.container.key;

import java.io.Serializable;
import java.lang.reflect.Type;

/**
 * @author rees.byars
 */
public interface KeyRepository extends Serializable {
    void addKey(SerializableKey key);
    <T> SerializableKey<T> retrieveKey(Class<T> cls);
    <T> SerializableKey<T> retrieveKey(Class<T> cls, String name);
    <T> SerializableKey<T> retrieveKey(Type type);
    <T> SerializableKey<T> retrieveKey(Type type, String name);
}
