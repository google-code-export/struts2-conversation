package com.github.overengineer.container.key;

import java.io.Serializable;
import java.lang.reflect.Type;

/**
 * @author rees.byars
 */
public interface KeyRepository extends Serializable {
    void addKey(SerializableKey key);
    SerializableKey retrieveKey(Class cls);
    SerializableKey retrieveKey(Class cls, String name);
    SerializableKey retrieveKey(Type type);
    SerializableKey retrieveKey(Type type, String name);
}
