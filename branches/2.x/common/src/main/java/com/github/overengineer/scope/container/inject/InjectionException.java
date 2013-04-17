package com.github.overengineer.scope.container.inject;

/**
 * @author rees.byars
 */
public class InjectionException extends RuntimeException {
    public InjectionException(String message, Exception root) {
        super(message, root);
    }
}
