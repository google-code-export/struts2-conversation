package com.github.overengineer.scope.container.standalone;

/**
 */
public class WiringException extends Exception {
    public WiringException(String message, Exception root) {
        super(message, root);
    }
}
