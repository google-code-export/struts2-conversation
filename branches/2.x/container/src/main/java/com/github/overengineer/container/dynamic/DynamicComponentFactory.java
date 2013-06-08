package com.github.overengineer.container.dynamic;

import com.github.overengineer.container.Provider;
import com.github.overengineer.container.key.Key;

import java.io.Serializable;

/**
 * a FactoryFactory that removes the need for all other FactoryFactories
 *
 * @author rees.byars
 */
public interface DynamicComponentFactory extends Serializable {
    <T> T createManagedComponentFactory(Class factoryInterface, Key producedTypeKey, Provider provider);
    <T> T createNonManagedComponentFactory(Class factoryInterface, Class concreteProducedType, Provider provider);
    <T> T createCompositeHandler(Class<T> targetInterface, Provider provider);
    <T> T createDelegatingService(Class<T> serviceInterface, Provider provider);
}
