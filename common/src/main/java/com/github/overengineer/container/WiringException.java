package com.github.overengineer.container;

/**
 * @author rees.byars
 */
public class WiringException extends Exception {
    public WiringException(String message, Exception root) {
        super(message, root);
    }
}
