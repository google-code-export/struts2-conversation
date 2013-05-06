package com.github.overengineer.container.proxy;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * @author <a href="mailto:jackknifebarber@users.sourceforge.net">Mike Martin</a>
 * @author rees.byars
 */
public class DefaultJdkProxyFactory implements JdkProxyFactory {

    private final Class<?> targetClass;
    private final Class[] interfaces;
    private transient Reference<Constructor> constructorReference;

    public DefaultJdkProxyFactory(Class targetClass) {
        this.targetClass = targetClass;
        List<Class<?>> interfaces = getAllInterfaces(targetClass);
        this.interfaces = (interfaces.toArray(new Class[interfaces.size()]));
    }

    @Override
    public Object newProxyInstance(InvocationHandler handler) {

        try {
            return getConstructor().newInstance(handler);
        } catch (Exception e) {
            throw new RuntimeException("There was an unexpected error proxying the class [" + targetClass.getName() + "]", e);
        }
    }

    private synchronized Constructor getConstructor() {

        Constructor constructor = constructorReference == null ? null : constructorReference.get();

        if (constructor == null) {
            try {
                constructor = Proxy.getProxyClass(getClass().getClassLoader(), interfaces).getConstructor(new Class[]{InvocationHandler.class});
            } catch (NoSuchMethodException e) {
                throw new RuntimeException("There was an unexpected error proxying the class [" + targetClass.getName() + "]", e);
            }

            constructorReference = new SoftReference<Constructor>(constructor);
        }

        return constructor;
    }

    private static List<Class<?>> getAllInterfaces(Class<?> cls) {

        if (cls == null) {
            return null;
        }

        LinkedHashSet<Class<?>> interfacesFound = new LinkedHashSet<Class<?>>();
        getAllInterfaces(cls, interfacesFound);
        return new ArrayList<Class<?>>(interfacesFound);
    }

    private static void getAllInterfaces(Class<?> cls, HashSet<Class<?>> interfacesFound) {
        while (cls != null) {
            Class<?>[] interfaces = cls.getInterfaces();

            for (Class<?> i : interfaces) {
                if (interfacesFound.add(i)) {
                    getAllInterfaces(i, interfacesFound);
                }
            }

            cls = cls.getSuperclass();
        }
    }
}
