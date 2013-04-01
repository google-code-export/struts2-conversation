package com.github.overengineer.scope.container;

public interface Container extends Provider {

    void verify() throws WiringException;

    Container loadModule(Module module);

    <T> Container add(Class<T> componentType, Class<? extends T> implementationType);

    <T, I extends T> Container addInstance(Class<T> componentType, I implementation);

    Container addProperty(String propertyName, Object propertyValue);

    Container addListener(ComponentInitializationListener listener);

}
