package com.github.overengineer.scope.container.alt;

import java.io.Serializable;

/**
 */
public interface ComponentInitializationListener extends Serializable {

    <T, TT extends T> TT onInitialization(T component);

}
