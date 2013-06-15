package com.github.overengineer.container.key;

import java.io.Serializable;
import java.lang.reflect.Type;

/**
 * @author rees.byars
 */
public interface Key<T> extends Serializable {
    Object getQualifier();
    Type getType();
    Class<? super T> getTargetClass();
}
