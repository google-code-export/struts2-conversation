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
    Class getTargetClass();

    abstract class Generic<T> implements Key {

        ParameterizedType parameterizedType;
        Type type;
        Class targetClass;

        public Generic() {
            parameterizedType = (ParameterizedType) getClass().getGenericSuperclass();
            type = parameterizedType.getActualTypeArguments()[0];
            if (type instanceof ParameterizedType) {
                targetClass = (Class) ((ParameterizedType) type).getRawType();
            } else {
                //TODO throw new KeyException("blah");
            }
        }

        @Override
        public int hashCode() {
            return type.hashCode();
        }

        @Override
        public Type getType() {
            return type;
        }

        @Override
        public Class getTargetClass() {
            return targetClass;
        }

        @Override
        public boolean equals(Object object) {
            return object instanceof Key && type.equals(((Key) object).getType());
        }
    }

    class Builder {

       public static Key fromType(final Type type) {
           return  new DefaultKey(type);
       }

        static class DefaultKey implements Key {

            Type type;
            Class targetClass;

            protected DefaultKey(Type type) {
                this.type = type;
                if (type instanceof Class) {
                    targetClass = (Class) type;
                } else if (type instanceof ParameterizedType) {
                    targetClass = (Class) ((ParameterizedType) type).getRawType();
                } else {
                    throw new UnsupportedOperationException();
                }
            }

            @Override
            public Type getType() {
                return type;
            }

            @Override
            public Class getTargetClass() {
                return targetClass;
            }

            @Override
            public int hashCode() {
                return type.hashCode();
            }

            @Override
            public boolean equals(Object object) {
                return object instanceof Key && type.equals(((Key) object).getType());
            }
        }
    }

}
