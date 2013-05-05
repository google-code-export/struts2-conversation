package com.github.overengineer.scope.container;

import java.util.List;
import java.util.Map;

/**
 * @author rees.byars
 */
public interface Module {

    Map<Class<?>, List<Class<?>>> getTypeMappings();

    Map<Class<?>, Object> getInstanceMappings();

    Map<Key, List<Class<?>>> getGenericTypeMappings();

    Map<Key, Object> getGenericInstanceMappings();

    Map<String, Object> getProperties();

}
