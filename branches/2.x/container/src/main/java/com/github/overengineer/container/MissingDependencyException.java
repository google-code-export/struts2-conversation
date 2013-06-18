package com.github.overengineer.container;

import com.github.overengineer.container.key.Key;

/**
 * @author rees.byars
 */
public class MissingDependencyException extends RuntimeException {
    public MissingDependencyException(Key key) {
        super("No components of type [" + key.getType() + "] with qualifier [" + key.getQualifier() + "] have been registered with the container");
    }
}
