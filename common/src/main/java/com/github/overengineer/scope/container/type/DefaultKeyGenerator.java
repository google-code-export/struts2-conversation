package com.github.overengineer.scope.container.type;

import java.lang.reflect.Type;

/**
 * @author rees.byars
 */
public class DefaultKeyGenerator implements KeyGenerator {

    @Override
    public SerializableKey fromClass(Class targetClass) {
        return new ClassKey(targetClass);
    }

    @Override
    public Key fromType(Type type) {
        return  new TempKey(type);
    }

}
