package com.github.overengineer.scope.container;

/**
 */
public class InjectionException extends RuntimeException {
    public InjectionException(String message, Exception root) {
        super(message, root);
    }
}
