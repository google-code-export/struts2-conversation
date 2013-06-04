package com.github.overengineer.container.key;

import java.lang.reflect.Type;

/**
 *
 * this class was built from scratch over a cup of coffee, but, on some level,
 * it no doubt owes some inspiration to bob lee.
 *
 * @author rees.byars
 *
 */
public interface BasicKey<T> {

    String getName();
    Type getType();
    Class<? super T> getTargetClass();

}
