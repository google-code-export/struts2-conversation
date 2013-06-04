package com.github.overengineer.container.key;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * @author rees.byars
 */
public class DefaultKeyRepository implements KeyRepository {

    private final Map<Object, SerializableKey> keys = new HashMap<Object, SerializableKey>();

    @Override
    public void addKey(SerializableKey key) {
        keys.put(key, key);
    }

    @Override
    public <T> SerializableKey<T> retrieveKey(Class<T> cls) {
        return new ClassKey<T>(cls);
    }

    @Override
    public <T> SerializableKey<T> retrieveKey(Class<T> cls, String name) {
        return new ClassKey<T>(cls, name);
    }

    @Override
    public <T> SerializableKey<T> retrieveKey(final Type type) {
        return retrieveKey(type, null);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> SerializableKey<T> retrieveKey(Type type, String name) {
        if (type instanceof Class) {
            return retrieveKey((Class) type, name);
        }
        SerializableKey<T> key = (SerializableKey<T>) keys.get(new TempKey<T>(type, name));
        if (key != null) {
            return key;
        }
        if (type instanceof ParameterizedType) {
            return new LazyDelegatingKey<T>(type, name);
        }
        throw new UnsupportedOperationException("Unsupported injection type [" + type + "]");
    }

    public class LazyDelegatingKey<T> extends DelegatingSerializableKey<T> {

        private volatile SerializableKey<T> key;
        private transient Type type;
        private String name;

        private LazyDelegatingKey(Type type, String name) {
            this.type = type;
            this.name = name;
        }

        @Override
        protected SerializableKey<T> getDelegateKey() {
            if (key == null) {
                synchronized (this) {
                    if (key == null) {
                        key = retrieveKey(type, name);
                    }
                }
            }
            return key;
        }

        private void writeObject(ObjectOutputStream out) throws IOException, ClassNotFoundException {
            getDelegateKey();
            out.defaultWriteObject();
        }

    }


}
