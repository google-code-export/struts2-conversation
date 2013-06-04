package com.github.overengineer.container.module;

import com.github.overengineer.container.key.GenericKey;

/**
 * @author rees.byars
 */
public class GenericMapping<T> extends TypeMapping<T> {

    @SuppressWarnings("unchecked")
    public GenericMapping(GenericKey<? extends T> genericKey) {
        super((Class<T>) genericKey.getTargetClass());
    }

}
