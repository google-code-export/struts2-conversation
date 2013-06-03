package com.github.overengineer.container;

import com.github.overengineer.container.key.SerializableKey;
import com.github.overengineer.container.module.Module;

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

    <T> Container add(Class<T> componentType, String name, Class<? extends T> implementationType);

    <T> Container add(SerializableKey key, Class<? extends T> implementationType);

    <T, I extends T> Container addInstance(Class<T> componentType, I implementation);

    <T, I extends T> Container addInstance(Class<T> componentType, String name, I implementation);

    <T, I extends T> Container addInstance(SerializableKey key, I implementation);

    Container addCustomProvider(Class providedType, Class<?> customProviderType);

    Container addCustomProvider(SerializableKey providedTypeKey, Class<?> customProviderType);

    Container addCustomProvider(Class providedType, Object customProvider);

    Container addCustomProvider(SerializableKey providedTypeKey, Object customProvider);

    Container registerManagedComponentFactory(SerializableKey factoryKey);

    Container registerNonManagedComponentFactory(SerializableKey factoryKey, Class producedType);

    Container registerCompositeTarget(Class<?> targetInterface);

    Container registerCompositeTarget(SerializableKey targetKey);

    List<ComponentInitializationListener> getInitializationListeners();

    List<Object> getAllComponents();

    List<Container> getCascadingContainers();

    List<Container> getChildren();

    Container getReal();

    ComponentStrategy<?> getStrategy(SerializableKey key, SelectionAdvisor ... advisors);

    Container makeInjectable();

}
