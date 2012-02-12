package com.google.code.rees.scope.session;

import java.io.Serializable;

/**
 * 
 * @author rees.byars
 * 
 */
public interface SessionManager extends Serializable {

    public void setConfigurationProvider(
            SessionConfigurationProvider configurationProvider);

    public void processSessionFields(SessionAdapter sessionAdapter);

}
