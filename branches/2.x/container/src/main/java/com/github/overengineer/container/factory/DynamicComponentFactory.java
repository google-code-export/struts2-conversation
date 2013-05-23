package com.github.overengineer.container.factory;

import com.github.overengineer.container.Provider;
import com.github.overengineer.container.key.SerializableKey;

import java.io.Serializable;

/**
 * a FactoryFactory that removes the need for all other FactoryFactories
 *
 * @author rees.byars
 */
public interface DynamicComponentFactory extends Serializable {
    <T> T createManagedComponentFactory(Class factoryInterface, SerializableKey producedTypeKey, Provider provider);
    <T> T createNonManagedComponentFactory(Class factoryInterface, Class concreteProducedType, Provider provider);
    <T> DynamicCompositeHandler<T> createCompositeHandler(Class<T> targetInterface);
}
