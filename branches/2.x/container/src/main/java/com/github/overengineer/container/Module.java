package com.github.overengineer.container;

import com.github.overengineer.container.key.SerializableKey;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author rees.byars
 */
public interface Module extends Serializable {

    Map<Class<?>, List<Class<?>>> getTypeMappings();

    Map<Class<?>, Object> getInstanceMappings();

    Map<SerializableKey, List<Class<?>>> getGenericTypeMappings();

    Map<SerializableKey, Object> getGenericInstanceMappings();

    Set<SerializableKey> getManagedComponentFactories();

    Map<SerializableKey, Class> getNonManagedComponentFactories();

    Map<String, Object> getProperties();

}
