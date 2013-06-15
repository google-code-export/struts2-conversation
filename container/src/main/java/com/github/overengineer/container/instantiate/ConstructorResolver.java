package com.github.overengineer.container.instantiate;

import com.github.overengineer.container.util.ConstructorRef;

import java.io.Serializable;

/**
 * @author rees.byars
 */
public interface ConstructorResolver extends Serializable {

    <T> ConstructorRef<T> resolveConstructor(Class<T> type, Class ... providedArgs);

}
