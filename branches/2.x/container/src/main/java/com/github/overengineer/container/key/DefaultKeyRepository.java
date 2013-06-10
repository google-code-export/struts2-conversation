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

    private final Map<Object, Key> keys = new HashMap<Object, Key>();

    @Override
    public void addKey(Key key) {
        keys.put(key, key);
    }

    @Override
    public <T> Key<T> retrieveKey(Class<T> cls) {
        return new ClassKey<T>(cls);
    }

    @Override
    public <T> Key<T> retrieveKey(Class<T> cls, Object qualifier) {
        return new ClassKey<T>(cls, qualifier);
    }

    @Override
    public <T> Key<T> retrieveKey(final Type type) {
        return retrieveKey(type, null);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Key<T> retrieveKey(Type type, Object qualifier) {
        if (type instanceof Class) {
            return retrieveKey((Class) type, qualifier);
        }
        Key<T> key = (Key<T>) keys.get(new TempKey<T>(type, qualifier));
        if (key != null) {
            return key;
        }
        if (type instanceof ParameterizedType) {
            return new LazyDelegatingKey<T>(type, qualifier);
        }
        throw new UnsupportedOperationException("Unsupported injection type [" + type + "]");
    }

    public class LazyDelegatingKey<T> extends DelegatingSerializableKey<T> {

        private volatile Key<T> key;
        private transient Type type;
        private Object qualifier;

        private LazyDelegatingKey(Type type, Object qualifier) {
            this.type = type;
            this.qualifier = qualifier;
        }

        @Override
        protected Key<T> getDelegateKey() {
            if (key == null) {
                synchronized (this) {
                    if (key == null) {
                        key = retrieveKey(type, qualifier);
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
