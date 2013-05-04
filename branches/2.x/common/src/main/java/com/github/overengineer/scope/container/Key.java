package com.github.overengineer.scope.container;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 *
 * this class was built from scratch over a cup of coffee, but, on some level,
 * it no doubt owes some inspiration to bob lee.
 *
 * @author rees.byars
 */
public interface Key {

    Type getType();

    abstract class Generic<T> implements Key {

        Type type = ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];

        @Override
        public int hashCode() {
            return type.hashCode();
        }

        @Override
        public Type getType() {
            return type;
        }

        @Override
        public boolean equals(Object object) {
            return object instanceof Key && type == ((Key) object).getType();
        }
    }

    class Builder {

       public static Key fromType(final Type type) {
           return  new DefaultKey(type);
       }

        static class DefaultKey implements Key {

            Type type;

            protected DefaultKey(Type type) {
                this.type = type;
            }

            @Override
            public Type getType() {
                return type;
            }

            @Override
            public int hashCode() {
                return type.hashCode();
            }

            @Override
            public boolean equals(Object object) {
                return object instanceof Key && type == ((Key) object).getType();
            }
        }
    }

}
