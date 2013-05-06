package com.github.overengineer.scope.container.type;

import java.lang.reflect.Type;

/**
 * @author rees.byars
 */
public interface KeyGenerator {
    SerializableKey fromClass(Class targetClass);
    Key fromType(Type type);
}
