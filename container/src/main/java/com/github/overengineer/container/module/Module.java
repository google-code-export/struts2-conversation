package com.github.overengineer.container.module;

import com.github.overengineer.container.key.Key;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author rees.byars
 */
public interface Module extends Serializable {

    List<Mapping<?>> getMappings();

    Set<Key> getManagedComponentFactories();

    Map<Key, Class> getNonManagedComponentFactories();

}
