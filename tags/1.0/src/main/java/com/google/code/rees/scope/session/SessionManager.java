package com.google.code.rees.scope.session;

import java.io.Serializable;

/**
 * Uses the {@link SessionConfigurationProvider} and {@link SessionAdapter} to
 * process, manage, and inject {@link SessionField SessionFields}.
 * 
 * @author rees.byars
 */
public interface SessionManager extends Serializable {

    /**
     * Set the {@link SessionConfigurationProvider}
     * 
     * @param configurationProvider
     */
    public void setConfigurationProvider(
            SessionConfigurationProvider configurationProvider);

    /**
     * Process, manage, and inject the {@link SessionField SessionFields}
     * 
     * @param sessionAdapter
     */
    public void processSessionFields(SessionAdapter sessionAdapter);

}
