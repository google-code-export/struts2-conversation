package com.github.overengineer.scope.container;

import java.io.Serializable;
import java.util.List;

/**
 * @author rees.byars
 */
public interface ComponentStrategy<T> extends Serializable {

    T get(Provider provider);

}