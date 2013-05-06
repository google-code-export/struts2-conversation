package com.github.overengineer.container;

import java.io.Serializable;

/**
 * @author rees.byars
 */
public interface PropertyProvider extends Serializable {

    <T> T getProperty(Class<T> clazz, String name);

}
