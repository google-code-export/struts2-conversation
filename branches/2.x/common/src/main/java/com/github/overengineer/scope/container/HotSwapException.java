package com.github.overengineer.scope.container;

/**
 */
public class HotSwapException extends Exception {
    public HotSwapException(Class<?> target, Class<?> currentImpl, Class<?> newImpl) {
        super("Could not swap the [" + target.getName() + "] implementation from [" + currentImpl.getName() + "] to [" + newImpl.getName() + "]");
    }
}
