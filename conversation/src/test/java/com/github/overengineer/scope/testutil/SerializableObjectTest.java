package com.github.overengineer.scope.testutil;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.easymock.EasyMock;
import org.junit.Test;


public abstract class SerializableObjectTest<T> {

    @Test
    public void testSerialization() throws Exception {
        String genericSuperClassName = this.getClass().getGenericSuperclass()
                .toString();
        String genericClassName = genericSuperClassName.substring(
                genericSuperClassName.indexOf('<') + 1,
                genericSuperClassName.indexOf('>'));
        @SuppressWarnings("unchecked")
        Class<T> serializableObjectClass = (Class<T>) Class
                .forName(genericClassName);

        T serializableObject = this.getInstance(serializableObjectClass);

        SerializationTestingUtil
                .getSerializedCopy((Serializable) serializableObject);
    }

    @SuppressWarnings("unchecked")
    public <TT> TT getInstance(Class<TT> tt) throws IllegalArgumentException,
            InstantiationException, IllegalAccessException,
            InvocationTargetException {
        Constructor<?> constructor = tt.getDeclaredConstructors()[0];
        Class<?>[] paramTypes = constructor.getParameterTypes();
        Object[] constructorArgs = new Object[paramTypes.length];
        for (int i = 0; i < paramTypes.length; i++) {
            Class<?> paramClass = paramTypes[i];
            if (paramClass.isInterface()) {
                constructorArgs[i] = EasyMock.createMock(paramClass);
            } else if (paramClass.isPrimitive()) {
                constructorArgs[i] = this.getPrimitiveInstance(paramClass);
            } else {
                constructorArgs[i] = getInstance(paramClass);
            }
        }
        TT instance = null;

        try {
            instance = (TT) constructor.newInstance(constructorArgs);
        } catch (InstantiationException ie) {
            instance = EasyMock.createMock(tt);
        }

        return instance;
    }

    @SuppressWarnings("unchecked")
    public <TTT> TTT getPrimitiveInstance(Class<TTT> ttt) {
        Object primitiveWrapper = null;
        if (ttt.equals(long.class)) {
            primitiveWrapper = 1L;
        }
        return (TTT) primitiveWrapper;
    }

}
