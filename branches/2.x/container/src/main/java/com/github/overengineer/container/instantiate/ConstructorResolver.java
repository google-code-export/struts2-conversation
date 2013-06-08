package com.github.overengineer.container.instantiate;

import java.io.Serializable;
import java.lang.reflect.Constructor;

/**
 * @author rees.byars
 */
public interface ConstructorResolver extends Serializable {

    <T> Constructor<T> resolveConstructor(Class<T> type, Class ... providedArgs);

}
