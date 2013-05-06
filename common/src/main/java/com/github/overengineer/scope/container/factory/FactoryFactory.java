package com.github.overengineer.scope.container.factory;

import com.github.overengineer.scope.container.Provider;
import com.github.overengineer.scope.container.key.Key;

/**
 * a FactoryFactory that removes the need for all other FactoryFactories
 *
 * @author rees.byars
 */
public interface FactoryFactory {
    <T> T createFactory(Class<T> factoryInterface, Key producedTypeKey, Provider provider);
}
