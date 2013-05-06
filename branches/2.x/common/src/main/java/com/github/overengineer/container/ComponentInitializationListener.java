package com.github.overengineer.container;

import java.io.Serializable;

/**
 * @author rees.byars
 */
public interface ComponentInitializationListener extends Serializable {

    <T> T onInitialization(T component);

}
