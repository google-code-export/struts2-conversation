package com.github.overengineer.container;

import com.github.overengineer.container.key.SerializableKey;

import java.io.Serializable;
import java.lang.reflect.Type;

/**
 * @author rees.byars
 */
public interface ComponentStrategyCache extends Serializable {

    <T> void add(Class<T> componentType, Class<? extends T> implementationType);

    void add(SerializableKey key, Class implementationType);

    <T, I extends T> void addInstance(Class<T> componentType, I implementation);

    void addInstance(SerializableKey key, Object implementation);

    <T> ComponentStrategy<T> get(Class<T> clazz);

    <T> ComponentStrategy<T> get(Type type);

    <T> ComponentStrategy<T> get(SerializableKey key);

}
