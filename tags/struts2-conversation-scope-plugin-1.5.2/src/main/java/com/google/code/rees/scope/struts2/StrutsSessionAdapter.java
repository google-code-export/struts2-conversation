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
 *  $Id: StrutsSessionAdapter.java reesbyars $
 ******************************************************************************/
package com.google.code.rees.scope.struts2;

import java.util.HashMap;
import java.util.Map;

import com.google.code.rees.scope.session.SessionAdapter;
import com.google.code.rees.scope.session.SessionPostProcessor;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.PreResultListener;

/**
 * The Struts2 implementation of the {@link SessionAdapter}.
 * 
 * @author rees.byars
 */
public class StrutsSessionAdapter extends SessionAdapter {

    private static final long serialVersionUID = -5521071699714160473L;

    protected ActionInvocation invocation;
    protected ActionContext actionContext;
    protected Map<String, Object> sessionContext;

    @SuppressWarnings("unchecked")
    public StrutsSessionAdapter(ActionInvocation invocation) {
        this.invocation = invocation;
        this.actionContext = invocation.getInvocationContext();
        sessionContext = (Map<String, Object>) actionContext.getSession().get(
                StrutsScopeConstants.SESSION_FIELD_MAP_KEY);
        if (sessionContext == null) {
            sessionContext = new HashMap<String, Object>();
            actionContext.getSession().put(
                    StrutsScopeConstants.SESSION_FIELD_MAP_KEY, sessionContext);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getAction() {
        return this.invocation.getAction();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getActionId() {
        return this.invocation.getProxy().getMethod();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Object> getSessionContext() {
        return this.sessionContext;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addPostProcessor(SessionPostProcessor sessionPostProcessor) {
        this.invocation.addPreResultListener(new SessionResultListener(this,
                sessionPostProcessor));
    }

    class SessionResultListener implements PreResultListener {

        private SessionPostProcessor postProcessor;
        private SessionAdapter sessionAdapter;

        SessionResultListener(SessionAdapter sessionAdapter,
                SessionPostProcessor postProcessor) {
            this.sessionAdapter = sessionAdapter;
            this.postProcessor = postProcessor;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void beforeResult(ActionInvocation invocation, String resultCode) {
            this.postProcessor.postProcessSession(sessionAdapter);
        }
    }

}
