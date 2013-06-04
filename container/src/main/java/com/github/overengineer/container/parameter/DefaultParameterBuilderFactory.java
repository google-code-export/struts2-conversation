package com.github.overengineer.container.parameter;

import com.github.overengineer.container.key.Key;
import com.github.overengineer.container.key.KeyRepository;
import com.github.overengineer.container.key.KeyUtil;
import com.github.overengineer.container.metadata.MetadataAdapter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * @author rees.byars
 */
public class DefaultParameterBuilderFactory implements ParameterBuilderFactory {

    private final MetadataAdapter metadataAdapter;
    private final KeyRepository keyRepository;

    public DefaultParameterBuilderFactory(MetadataAdapter metadataAdapter, KeyRepository keyRepository) {
        this.metadataAdapter = metadataAdapter;
        this.keyRepository = keyRepository;
    }

    @Override
    public <T> ParameterBuilder<T> create(Class<T> injectionTarget, Constructor<T> constructor, Class[] trailingArgTypes) {

        Type[] genericParameterTypes = constructor.getGenericParameterTypes();
        Annotation[][] annotations = constructor.getParameterAnnotations();

        return createBuilder(createProxies(injectionTarget, genericParameterTypes, annotations, trailingArgTypes));

    }

    @Override
    public <T> ParameterBuilder<T> create(Class<T> injectionTarget, Method method, Class[] trailingArgTypes) {

        Type[] genericParameterTypes = method.getGenericParameterTypes();
        Annotation[][] annotations = method.getParameterAnnotations();

        return createBuilder(createProxies(injectionTarget, genericParameterTypes, annotations, trailingArgTypes));
    }

    @SuppressWarnings("unchecked")
    protected ParameterProxy[] createProxies(Class<?> injectionTarget, Type[] genericParameterTypes, Annotation[][] annotations, Class[] trailingArgs) {

        ParameterProxy[] parameterProxies = new ParameterProxy[genericParameterTypes.length - trailingArgs.length];
        for (int i = 0; i < parameterProxies.length; i++) {
            parameterProxies[i] = createProxy(injectionTarget, genericParameterTypes[i], annotations[i]);
        }

        return parameterProxies;
    }

    @SuppressWarnings("unchecked")
    protected  <T> ParameterProxy<T> createProxy(Class<?> injectionTarget, Type parameterType, Annotation[] annotations) {

        Key key = keyRepository.retrieveKey(parameterType, metadataAdapter.getName(parameterType, annotations));

        if (KeyUtil.getClass(parameterType).isAssignableFrom(injectionTarget)) {
            return new DecoratorParameterProxy<T>(key, injectionTarget);
        }

        return new ComponentParameterProxy<T>(key);

    }

    protected <T> ParameterBuilder<T> createBuilder(ParameterProxy[] proxies) {

        boolean decorator = false;

        for (ParameterProxy proxy : proxies) {
            if (proxy instanceof DecoratorParameterProxy) {
                decorator = true;
                break;
            }
        }

        return new DefaultParameterBuilder<T>(proxies, decorator);

    }

}
