package com.github.overengineer.scope.container;

/**
 * @author rees.byars
 */
public class CircularReferenceException  extends RuntimeException {
    protected CircularReferenceException(String message) {
        super(message);
    }
}
