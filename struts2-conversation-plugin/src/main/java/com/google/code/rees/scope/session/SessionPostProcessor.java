package com.google.code.rees.scope.session;

import java.io.Serializable;

/**
 * This interface allows for post-processing of {@link SessionField
 * SessionFields}.
 * Registered via {@link SessionAdapter#addPostProcessor(SessionPostProcessor)}.
 * 
 * @author rees.byars
 */
public interface SessionPostProcessor extends Serializable {

    /**
     * Perform the post processing
     * 
     * @param sessionAdapter
     */
    public void postProcessSession(SessionAdapter sessionAdapter);

}
