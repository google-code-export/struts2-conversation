package com.github.overengineer.container;

import com.github.overengineer.container.key.SerializableKey;

import java.util.List;

/**
 * @author rees.byars
 */
public interface Container extends Provider {

    void verify() throws WiringException;

    Container loadModule(Class<? extends Module> module);

    Container addCascadingContainer(Container container);

    Container addChild(Container container);

    Container newEmptyClone();

    Container addListener(Class<? extends ComponentInitializationListener> listenerClass);

    <T> Container add(Class<T> componentType, Class<? extends T> implementationType);

    <T> Container add(SerializableKey key, Class<? extends T> implementationType);

    <T, I extends T> Container addInstance(Class<T> componentType, I implementation);

    <T, I extends T> Container addInstance(SerializableKey key, I implementation);

    Container registerManagedComponentFactory(SerializableKey factoryKey);

    Container registerNonManagedComponentFactory(SerializableKey factoryKey, Class producedType);

    Container addProperty(String propertyName, Object propertyValue);

    List<ComponentInitializationListener> getInitializationListeners();

    List<Object> getAllComponents();

    List<Container> getCascadingContainers();

    List<Container> getChildren();

    Container getReal();

    Container makeInjectable();

}
