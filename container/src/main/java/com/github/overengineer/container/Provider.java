package com.github.overengineer.container;

import com.github.overengineer.container.key.SerializableKey;

import java.io.Serializable;
import java.util.List;

/**
 * @author rees.byars
 */
public interface Provider extends Serializable {
    <T> T get(Class<T> clazz, SelectionAdvisor ... advisors);
    <T> T get(Class<T> clazz, String name, SelectionAdvisor ... advisors);
    <T> T get(SerializableKey<T> key, SelectionAdvisor ... advisors);
    <T> List<T> getAll(Class<T> clazz, SelectionAdvisor ... advisors);
    <T> List<T> getAll(Class<T> clazz, String name, SelectionAdvisor ... advisors);
    <T> List<T> getAll(SerializableKey<T> key, SelectionAdvisor ... advisors);
}
