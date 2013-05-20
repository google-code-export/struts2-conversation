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
    public SerializableKey retrieveKey(Class cls) {
        SerializableKey key = keys.get(cls);
        if (key == null) {
            key = new ClassKey(cls);
            keys.put(cls, key);
        }
        return key;
    }

    @Override
    public SerializableKey retrieveKey(final Type type) {
        SerializableKey key = keys.get(type);
        if (key != null) {
            return key;
        }
        if (type instanceof Class) {
            return retrieveKey((Class) type);
        }
        if (type instanceof ParameterizedType) {
            return new LazyDelegatingKey(type);
        }
        throw new UnsupportedOperationException("Unsupported injection type [" + type + "]");
    }

    public class LazyDelegatingKey extends DelegatingSerializableKey {

        private volatile SerializableKey key;
        private transient Type type;

        private LazyDelegatingKey(Type type) {
            this.type = type;
        }

        @Override
        protected SerializableKey getDelegateKey() {
            if (key == null) {
                synchronized (this) {
                    if (key == null) {
                        key = keys.get(new TempKey(type));
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
