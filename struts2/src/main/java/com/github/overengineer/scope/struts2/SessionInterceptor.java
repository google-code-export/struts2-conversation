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
package com.github.overengineer.scope.struts2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.overengineer.scope.ScopeContainer;
import com.github.overengineer.scope.ScopeContainerProvider;
import com.github.overengineer.scope.session.SessionManager;
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
    protected ScopeContainer scopeContainer;
    
    @Inject
    public void setScopeContainerProvider(ScopeContainerProvider scopeContainerProvider) {
    	scopeContainer = scopeContainerProvider.getScopeContainer();
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
    	
    	sessionManager = scopeContainer.getComponent(SessionManager.class);
        
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
