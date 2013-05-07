package com.github.overengineer.scope;

import java.io.Serializable;

/**
 * @author rees.byars
 */
public interface Factory<T> extends Serializable {
    T create();
}
