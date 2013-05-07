package com.github.overengineer.container.factory;

import com.github.overengineer.container.Provider;
import com.github.overengineer.container.key.Key;
import com.github.overengineer.container.key.SerializableKey;

/**
 * a FactoryFactory that removes the need for all other FactoryFactories
 *
 * @author rees.byars
 */
public interface FactoryFactory {
    <T> T createFactory(Class<T> factoryInterface, SerializableKey producedTypeKey, Provider provider);
}
