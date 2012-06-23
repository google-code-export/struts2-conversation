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
 *  $Id: DefaultSessionManager.java reesbyars $
 ******************************************************************************/
package com.google.code.rees.scope.session;

import java.lang.reflect.Field;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.code.rees.scope.util.InjectionUtil;

/**
 * The default implementation of the {@link SessionManager}. Also
 * implements {@link SessionPostProcessor} to get field values following
 * action execution.
 * 
 * @author rees.byars
 */
public class DefaultSessionManager implements SessionManager, SessionPostProcessor {

    private static final long serialVersionUID = -4835858908197930639L;
    private static final Logger LOG = LoggerFactory.getLogger(DefaultSessionManager.class);
    protected SessionConfigurationProvider configurationProvider;

    /**
     * {@inheritDoc}
     */
    @Override
    public void setConfigurationProvider(SessionConfigurationProvider configurationProvider) {
        this.configurationProvider = configurationProvider;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void processSessionFields(SessionAdapter sessionAdapter) {
        Object action = sessionAdapter.getAction();
        Class<?> actionClass = action.getClass();
        SessionConfiguration configuration = this.configurationProvider.getSessionConfiguration(actionClass);
        Map<String, Field> classSessionFields = configuration.getFields(actionClass);

        if (classSessionFields != null) {

            if (LOG.isDebugEnabled()) {
                LOG.debug("Attempting to inject session fields before executing " + sessionAdapter.getActionId());
            }

            Map<String, Object> sessionContext = sessionAdapter.getSessionContext();
            InjectionUtil.setFieldValues(action, classSessionFields, sessionContext);
            sessionAdapter.addPostProcessor(this);

        } else if (LOG.isDebugEnabled()) {
            LOG.debug("No session field were found for the action " + sessionAdapter.getActionId());
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void postProcessSession(SessionAdapter sessionAdapter) {

        Object action = sessionAdapter.getAction();
        Class<?> actionClass = action.getClass();
        SessionConfiguration configuration = this.configurationProvider.getSessionConfiguration(actionClass);
        Map<String, Field> classSessionFields = configuration.getFields(actionClass);

        if (classSessionFields != null) {

            if (LOG.isDebugEnabled()) {
                LOG.debug("Getting SessionField values following action execution from action of class " + action.getClass());
            }

            Map<String, Object> classSessionFieldValues = InjectionUtil.getFieldValues(action, classSessionFields);
            Map<String, Object> sessionContext = sessionAdapter.getSessionContext();
            sessionContext.putAll(classSessionFieldValues);

        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("No SessionFields found for class " + action.getClass());
            }
        }
    }

}
