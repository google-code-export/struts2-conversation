package com.github.overengineer.scope.container;

public interface StandaloneContainer extends ScopeContainer {

    StandaloneContainer loadModule(Module module);

    <T> StandaloneContainer add(Class<T> componentType, Class<? extends T> implementationType);

    <T, I extends T> StandaloneContainer addInstance(Class<T> componentType, I implementation);

    StandaloneContainer addProperty(String propertyName, Object propertyValue);

}
