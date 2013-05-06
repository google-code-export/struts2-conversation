package com.github.overengineer.container.inject;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.github.overengineer.container.Component;
import com.github.overengineer.container.Property;
import com.github.overengineer.container.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.overengineer.scope.util.ReflectionUtil;

/**
 * @author rees.byars
 */
public interface Injector<T> extends Serializable {

    void inject(T component, Provider provider);

    class CacheBuilder {

        private static Logger LOG = LoggerFactory.getLogger(CacheBuilder.class);

        public static <T, TT extends T> Set<Injector<T>> build(Class<TT> implementationType) {

            if (implementationType.isInterface()) {
                throw new RuntimeException("Type [{}] is an interface.  Cannot build injector cache for an interface.");
            }

            LOG.trace("Building injectors for component of type [{}]", implementationType.getName());

            Set<Injector<T>> injectors = new HashSet<Injector<T>>();
            for (Method method : implementationType.getMethods()) {
                if (ReflectionUtil.isPublicSetter(method)) {
                    Class<?> type = method.getParameterTypes()[0];
                    if (ReflectionUtil.isPropertyType(type) && method.isAnnotationPresent(Property.class)) {
                        Property property = method.getAnnotation(Property.class);
                        injectors.add(new PropertyInjector<T>(method, property.value(), type));
                    } else if (method.isAnnotationPresent(Component.class)) {
                        injectors.add(new ComponentInjector<T>(method, type));
                    }
                }
            }
            if (injectors.size() == 0) {
                return Collections.emptySet();
            } else {
                return injectors;
            }
        }
    }

}