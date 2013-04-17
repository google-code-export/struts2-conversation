package com.github.overengineer.scope.container;

/**
 * @author rees.byars
 */
public class WiringException extends Exception {
    public WiringException(String message, Exception root) {
        super(message, root);
    }
}
