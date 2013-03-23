package com.github.overengineer.scope.container;

import java.util.Map;

public interface Module {
    Map<Class<?>, Class<?>> getComponentMappings();

    Map<String, Object> getProperties();
}
