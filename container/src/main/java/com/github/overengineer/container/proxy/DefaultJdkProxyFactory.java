package com.github.overengineer.container.proxy;

import com.github.overengineer.container.util.ReflectionUtil;

import java.lang.ref.SoftReference;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * @author <a href="mailto:jackknifebarber@users.sourceforge.net">Mike Martin</a>
 * @author rees.byars
 */
public class DefaultJdkProxyFactory implements JdkProxyFactory {

    private final Class<?> targetClass;
    private final Class[] interfaces;
    private transient volatile SoftReference<Constructor> constructorReference;

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

    private Constructor getConstructor() {

        Constructor constructor = constructorReference == null ? null : constructorReference.get();

        if (constructor == null) {
            synchronized (this) {
                constructor = constructorReference == null ? null : constructorReference.get();
                if (constructor == null) {
                    try {
                        constructor = Proxy.getProxyClass(getClass().getClassLoader(), interfaces).getConstructor(new Class[]{InvocationHandler.class});
                        constructorReference = new SoftReference<Constructor>(constructor);
                    } catch (NoSuchMethodException e) {
                        throw new RuntimeException("There was an unexpected error proxying the class [" + targetClass.getName() + "]", e);
                    }
                }
            }
        }

        return constructor;
    }

    private static List<Class<?>> getAllInterfaces(Class<?> cls) {

        if (cls == null) {
            return null;
        }

        LinkedHashSet<Class<?>> interfacesFound = new LinkedHashSet<Class<?>>();
        ReflectionUtil.getAllInterfaces(cls, interfacesFound);
        return new ArrayList<Class<?>>(interfacesFound);
    }

}
