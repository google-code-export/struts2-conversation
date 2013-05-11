package com.github.overengineer.container.inject;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.github.overengineer.container.Provider;
import com.github.overengineer.container.metadata.DefaultMetadataAdapter;
import com.github.overengineer.container.metadata.MetadataAdapter;
import com.github.overengineer.container.util.ReflectionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

            MetadataAdapter metadataAdapter = new DefaultMetadataAdapter();

            Set<Injector<T>> injectors = new HashSet<Injector<T>>();
            for (Method method : implementationType.getMethods()) {
                if (ReflectionUtil.isPublicSetter(method)) {
                    Type type = method.getGenericParameterTypes()[0];
                    String propertyName = metadataAdapter.getPropertyName(method);
                    if (propertyName != null) {
                        injectors.add(new PropertyInjector<T>(method, propertyName, type));
                    } else if (metadataAdapter.isComponentSetter(method)) {
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
