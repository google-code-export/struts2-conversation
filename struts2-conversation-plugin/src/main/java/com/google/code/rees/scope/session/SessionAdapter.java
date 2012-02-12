package com.google.code.rees.scope.session;

import java.io.Serializable;
import java.util.Map;

/**
 * 
 * @author rees.byars
 * 
 */
public abstract class SessionAdapter implements Serializable {

    private static final long serialVersionUID = -3486485156666333845L;
    protected static ThreadLocal<SessionAdapter> sessionAdapter = new ThreadLocal<SessionAdapter>();

    public abstract Object getAction();

    public abstract String getActionId();

    public abstract Map<String, Object> getSessionContext();

    public abstract void addPostProcessor(
            SessionPostProcessor sessionPostProcessor);

    public static void setAdapter(SessionAdapter adapter) {
        sessionAdapter.set(adapter);
    }

    public static SessionAdapter getAdapter() {
        return sessionAdapter.get();
    }
}
