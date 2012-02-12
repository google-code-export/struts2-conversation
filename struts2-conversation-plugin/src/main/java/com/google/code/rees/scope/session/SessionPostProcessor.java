package com.google.code.rees.scope.session;

import java.io.Serializable;

/**
 * 
 * @author rees.byars
 * 
 */
public interface SessionPostProcessor extends Serializable {

    public void postProcessSession(SessionAdapter sessionAdapter);

}
