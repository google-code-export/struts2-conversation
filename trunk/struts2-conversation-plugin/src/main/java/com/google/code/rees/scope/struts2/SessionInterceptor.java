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
 *  $Id: SessionInterceptor.java reesbyars $
 ******************************************************************************/
package com.google.code.rees.scope.struts2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.code.rees.scope.ActionProvider;
import com.google.code.rees.scope.session.SessionConfigurationProvider;
import com.google.code.rees.scope.session.SessionManager;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.interceptor.Interceptor;

/**
 * A Struts2 {@link Interceptor} that uses bi-jects @SessionField annotated fields in action classes.
 * 
 * @author rees.byars
 */
public class SessionInterceptor implements Interceptor {

    private static final long serialVersionUID = 3222190171260674636L;
    private static final Logger LOG = LoggerFactory.getLogger(SessionInterceptor.class);

    protected SessionManager sessionManager;
    protected SessionConfigurationProvider sessionConfigurationProvider;
    protected ActionProvider finder;

    @Inject(StrutsScopeConstants.ACTION_FINDER_KEY)
    public void setActionClassFinder(ActionProvider finder) {
        this.finder = finder;
    }

    @Inject(StrutsScopeConstants.SESSION_MANAGER_KEY)
    public void setSessionManager(SessionManager manager) {
        this.sessionManager = manager;
    }

    @Inject(StrutsScopeConstants.SESSION_CONFIG_PROVIDER_KEY)
    public void setSessionConfigurationProvider(SessionConfigurationProvider sessionConfigurationProvider) {
        this.sessionConfigurationProvider = sessionConfigurationProvider;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void destroy() {
        LOG.info("Destroying the SessionInterceptor...");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init() {

    	LOG.info("Initializing the Session Interceptor...");

        try {
			this.sessionConfigurationProvider.init(finder.getActionClasses());
		} catch (Exception e) {
			LOG.warn(e.getMessage());
		}
        this.sessionManager.setConfigurationProvider(sessionConfigurationProvider);
        
        LOG.info("...Session Interceptor successfully initialized.");

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        this.sessionManager.processSessionFields(new StrutsSessionAdapter(invocation));
        return invocation.invoke();
    }

}
