package com.github.overengineer.scope.container;

/**
 */
public class MissingDependencyException extends RuntimeException {
    public MissingDependencyException(String message) {
        super(message);
    }
    public MissingDependencyException(Exception root) {
        super(root);
    }
    public MissingDependencyException(String message, Exception root) {
        super(message, root);
    }
}
