package com.github.overengineer.container.parameter;

import com.github.overengineer.container.key.Key;
import com.github.overengineer.container.key.KeyUtil;
import com.github.overengineer.container.key.Locksmith;
import com.github.overengineer.container.metadata.MetadataAdapter;
import com.github.overengineer.container.util.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * @author rees.byars
 */
public class TrailingsArgsParameterBuilderFactory implements ParameterBuilderFactory {

    private final MetadataAdapter metadataAdapter;

    public TrailingsArgsParameterBuilderFactory(MetadataAdapter metadataAdapter) {
        this.metadataAdapter = metadataAdapter;
    }

    @Override
    public <T> ParameterBuilder<T> create(Class<T> injectionTarget, Constructor<T> constructor, Class[] providedArgs) {
        return create(injectionTarget, new ConstructorRefImpl<T>(constructor), providedArgs);
    }

    @Override
    public <T> ParameterBuilder<T> create(Class<T> injectionTarget, Method method, Class[] providedArgs) {
        return create(injectionTarget, new MethodRefImpl(method), providedArgs);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> ParameterBuilder<T> create(Class<?> injectionTarget, ParameterizedFunction parameterizedFunction, Class[] providedArgs) {

        Type[] genericParameterTypes = parameterizedFunction.getParameterTypes();
        Annotation[][] annotations = parameterizedFunction.getParameterAnnotations();

        ParameterProxy[] parameterProxies = new ParameterProxy[genericParameterTypes.length - providedArgs.length];
        for (int i = 0; i < parameterProxies.length; i++) {
            ParameterRef parameterRef = new ParameterRefImpl(parameterizedFunction, i);
            parameterProxies[i] = createProxy(injectionTarget, parameterRef, annotations[i]);
        }

        return createBuilder(parameterProxies);
    }

    @SuppressWarnings("unchecked")
    protected  <T> ParameterProxy<T> createProxy(Class<?> injectionTarget, ParameterRef parameterRef, Annotation[] annotations) {

        Type parameterType = parameterRef.getType();

        Key key = Locksmith.makeKey(parameterRef, metadataAdapter.getQualifier(parameterType, annotations));

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

        return new TrailingArgsParameterBuilder<T>(proxies, decorator);

    }

}
