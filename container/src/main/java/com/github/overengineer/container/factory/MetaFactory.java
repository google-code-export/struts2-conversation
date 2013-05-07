package com.github.overengineer.container.factory;

import com.github.overengineer.container.Provider;
import com.github.overengineer.container.key.Key;
import com.github.overengineer.container.key.SerializableKey;

import java.io.Serializable;

/**
 * a FactoryFactory that removes the need for all other FactoryFactories
 *
 * @author rees.byars
 */
public interface MetaFactory extends Serializable {
    <T> T createFactory(Class factoryInterface, SerializableKey producedTypeKey, Provider provider);
}
