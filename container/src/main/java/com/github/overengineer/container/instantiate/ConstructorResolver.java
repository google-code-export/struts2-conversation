package com.github.overengineer.container.instantiate;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Type;

/**
 * @author rees.byars
 */
public interface ConstructorResolver extends Serializable {

    <T> Constructor<T> resolveConstructor(Class<T> type, Callback callback);

    interface Callback {
        void onResolution(Type[] genericParameterTypes, Annotation[][] annotations);
    }

}
