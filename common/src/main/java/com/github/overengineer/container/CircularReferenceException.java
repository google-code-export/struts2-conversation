package com.github.overengineer.container;

/**
 * @author rees.byars
 */
public class CircularReferenceException  extends RuntimeException {
    protected CircularReferenceException(String message) {
        super(message);
    }
}
