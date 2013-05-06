package com.github.overengineer.container;

/**
 * @author rees.byars
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
