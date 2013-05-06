package com.github.overengineer.scope.container.key;

import java.io.Serializable;
import java.lang.reflect.Type;

/**
 * @author rees.byars
 */
public interface KeyGenerator extends Serializable {
    SerializableKey fromClass(Class targetClass);
    Key fromType(Type type);
}
