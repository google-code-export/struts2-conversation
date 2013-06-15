package com.github.overengineer.container.module;

import com.github.overengineer.container.key.Key;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author rees.byars
 */
public interface Module extends Serializable {

    List<Mapping<?>> getMappings();

    Map<Key, Class> getNonManagedComponentFactories();

}
