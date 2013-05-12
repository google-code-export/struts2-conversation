package com.github.overengineer.container.key;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * @author rees.byars
 */
public class DefaultKeyRepository implements KeyRepository {

    private final Map<Key, SerializableKey> keys = new HashMap<Key, SerializableKey>();

    @Override
    public void addKey(SerializableKey key) {
        keys.put(key, key);
    }

    @Override
    public SerializableKey retrieveKey(Class cls) {
        SerializableKey key = new ClassKey(cls);
        keys.put(key, key);
        return key;
    }

    @Override
    public SerializableKey retrieveKey(Type type) {
        Key temp = new TempKey(type);
        SerializableKey key = keys.get(temp);
        if (key != null) {
            return key;
        }
        if (type instanceof Class) {
            return retrieveKey((Class) type);
        }
        throw new UnsupportedOperationException("No key exists for type [" + type + "]");
    }

}
