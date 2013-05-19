package com.github.overengineer.container.metadata;

/**
 * @author rees.byars
 */
public class MetadataException extends RuntimeException {
    public MetadataException(String message, Throwable root) {
        super(message, root);
    }
}
