package com.github.overengineer.scope.container;

import com.github.overengineer.scope.container.type.SerializableKey;

import java.util.List;
import java.util.Map;

/**
 * @author rees.byars
 */
public interface Module {

    Map<Class<?>, List<Class<?>>> getTypeMappings();

    Map<Class<?>, Object> getInstanceMappings();

    Map<SerializableKey, List<Class<?>>> getGenericTypeMappings();

    Map<SerializableKey, Object> getGenericInstanceMappings();

    Map<String, Object> getProperties();

}
