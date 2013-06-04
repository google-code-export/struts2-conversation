package com.github.overengineer.container;

import com.github.overengineer.container.key.Key;
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

    <T> Container add(Key<T> key, Class<? extends T> implementationType);

    <T, I extends T> Container addInstance(Class<T> componentType, I implementation);

    <T, I extends T> Container addInstance(Class<T> componentType, String name, I implementation);

    <T, I extends T> Container addInstance(Key<T> key, I implementation);

    Container addCustomProvider(Class<?> providedType, Class<?> customProviderType);

    Container addCustomProvider(Key<?> providedTypeKey, Class<?> customProviderType);

    Container addCustomProvider(Class<?> providedType, Object customProvider);

    Container addCustomProvider(Key<?> providedTypeKey, Object customProvider);

    Container registerManagedComponentFactory(Key<?> factoryKey);

    Container registerNonManagedComponentFactory(Key<?> factoryKey, Class producedType);

    Container registerCompositeTarget(Class<?> targetInterface);

    Container registerCompositeTarget(Key<?> targetKey);

    List<ComponentInitializationListener> getInitializationListeners();

    List<Object> getAllComponents();

    List<Container> getCascadingContainers();

    List<Container> getChildren();

    Container getReal();

    <T> ComponentStrategy<T> getStrategy(Key<T> key, SelectionAdvisor ... advisors);

    <T> List<ComponentStrategy<T>> getAllStrategies(Key<T> key, SelectionAdvisor... advisors);

    Container makeInjectable();

}
