package com.github.overengineer.scope.container;

import com.github.overengineer.scope.container.key.SerializableKey;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author rees.byars
 */
public interface Module {

    Map<Class<?>, List<Class<?>>> getTypeMappings();

    Map<Class<?>, Object> getInstanceMappings();

    Map<SerializableKey, List<Class<?>>> getGenericTypeMappings();

    Map<SerializableKey, Object> getGenericInstanceMappings();

    Set<SerializableKey> getRegisteredFactories();

    Map<String, Object> getProperties();

}
