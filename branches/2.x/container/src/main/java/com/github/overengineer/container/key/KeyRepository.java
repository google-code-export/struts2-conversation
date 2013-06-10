package com.github.overengineer.container.key;

import java.io.Serializable;
import java.lang.reflect.Type;

/**
 * @author rees.byars
 */
public interface KeyRepository extends Serializable {
    void addKey(Key key);
    <T> Key<T> retrieveKey(Class<T> cls);
    <T> Key<T> retrieveKey(Class<T> cls, Object qualifier);
    <T> Key<T> retrieveKey(Type type);
    <T> Key<T> retrieveKey(Type type, Object qualifier);
}
