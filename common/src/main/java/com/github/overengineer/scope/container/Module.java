package com.github.overengineer.scope.container;

import java.util.List;
import java.util.Map;

public interface Module {
    Map<Class<?>, List<Class<?>>> getTypeMappings();
    Map<Class<?>, Object> getInstanceMappings();
    Map<String, Object> getProperties();
}
