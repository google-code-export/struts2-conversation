package com.github.overengineer.scope.container;

/**
 */
public class WiringException extends Exception {
    public WiringException(String message, Exception root) {
        super(message, root);
    }
}
