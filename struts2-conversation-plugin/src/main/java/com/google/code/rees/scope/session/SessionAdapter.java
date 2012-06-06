/*******************************************************************************
 * 
 *  Struts2-Conversation-Plugin - An Open Source Conversation- and Flow-Scope Solution for Struts2-based Applications
 *  =================================================================================================================
 * 
 *  Copyright (C) 2012 by Rees Byars
 *  http://code.google.com/p/struts2-conversation/
 * 
 * **********************************************************************************************************************
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 *  the License. You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 *  an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 *  specific language governing permissions and limitations under the License.
 * 
 * **********************************************************************************************************************
 * 
 *  $Id: SessionAdapter.java reesbyars $
 ******************************************************************************/
package com.google.code.rees.scope.session;

import java.io.Serializable;
import java.util.Map;

/**
 * A simple abstract adapter used to adapt frameworks
 * to the session management components. Makes use of {@link ThreadLocal} to
 * make the current request's adapter available through the static call
 * <code>SessionAdapter.<i>getAdapter()</i></code>.
 * 
 * @author rees.byars
 */
public abstract class SessionAdapter implements Serializable {

    private static final long serialVersionUID = -3486485156666333845L;
    protected static ThreadLocal<SessionAdapter> sessionAdapter = new ThreadLocal<SessionAdapter>();

    /**
     * The controller instance, such as a Struts2 action class or a Spring MVC
     * controller
     * 
     * @return
     */
    public abstract Object getAction();

    /**
     * A string identifying the current action. The convention employed
     * by the {@link DefaultSessionConfigurationProvider} is the name of the
     * controller method being executed.
     * 
     * @return
     */
    public abstract String getActionId();

    /**
     * Returns a session-scoped map. For Struts2, the returned map is the
     * Struts2 SessionMap.
     * 
     * @return
     */
    public abstract Map<String, Object> getSessionContext();

    /**
     * Add a {@link SessionPostProcessor} that is guaranteed to be
     * executed after action execution. Calling
     * {@link SessionPostProcessor#postProcessSession(SessionAdapter)} is the
     * responsibility of the concrete SessionAdapter extending
     * class.
     * 
     * @param sessionPostProcessor
     */
    public abstract void addPostProcessor(SessionPostProcessor sessionPostProcessor);

    /**
     * Set the {@link ThreadLocal} SessionAdapter for use with the current
     * request. Called in the constructor to force new instances into
     * the ThreadLocal.
     * 
     * @param adapter
     */
    public static void setAdapter(SessionAdapter adapter) {
        sessionAdapter.set(adapter);
    }

    /**
     * Get the {@link ThreadLocal} SessionAdapter associated with the
     * current request
     * 
     * @return
     */
    public static SessionAdapter getAdapter() {
        return sessionAdapter.get();
    }
}
