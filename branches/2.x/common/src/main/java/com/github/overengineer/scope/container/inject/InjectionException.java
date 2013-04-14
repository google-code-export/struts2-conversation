package com.github.overengineer.scope.container.inject;

/**
 */
public class InjectionException extends RuntimeException {
    public InjectionException(String message, Exception root) {
        super(message, root);
    }
}
