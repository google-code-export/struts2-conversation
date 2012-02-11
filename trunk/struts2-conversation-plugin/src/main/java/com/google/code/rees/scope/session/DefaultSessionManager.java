package com.google.code.rees.scope.session;

import java.lang.reflect.Field;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.code.rees.scope.util.ScopeUtil;

public class DefaultSessionManager implements SessionManager,
        SessionPostProcessor {

    private static final long serialVersionUID = -4835858908197930639L;

    private static final Logger LOG = LoggerFactory
            .getLogger(DefaultSessionManager.class);

    protected SessionConfigurationProvider configurationProvider;

    @Override
    public void setConfigurationProvider(
            SessionConfigurationProvider configurationProvider) {
        this.configurationProvider = configurationProvider;

    }

    @Override
    public void processSessionFields(SessionAdapter sessionAdapter) {
        Object action = sessionAdapter.getAction();
        Class<?> actionClass = action.getClass();
        SessionConfiguration configuration = this.configurationProvider
                .getSessionConfiguration(actionClass);
        Map<String, Field> classSessionFields = configuration
                .getFields(actionClass);

        if (classSessionFields != null) {

            if (LOG.isDebugEnabled()) {
                LOG.debug("Attempting to inject session fields before executing "
                        + sessionAdapter.getActionId());
            }

            Map<String, Object> sessionContext = sessionAdapter
                    .getSessionContext();

            ScopeUtil
                    .setFieldValues(action, classSessionFields, sessionContext);

            sessionAdapter.addPostProcessor(this);

        } else if (LOG.isDebugEnabled()) {
            LOG.debug("No session field were found for the action "
                    + sessionAdapter.getActionId());
        }

    }

    @Override
    public void postProcessSession(SessionAdapter sessionAdapter) {

        Object action = sessionAdapter.getAction();
        Class<?> actionClass = action.getClass();
        SessionConfiguration configuration = this.configurationProvider
                .getSessionConfiguration(actionClass);
        Map<String, Field> classSessionFields = configuration
                .getFields(actionClass);

        if (classSessionFields != null) {

            if (LOG.isDebugEnabled()) {
                LOG.debug("Getting SessionField values following action execution from action of class "
                        + action.getClass());
            }

            Map<String, Object> classSessionFieldValues = ScopeUtil
                    .getFieldValues(action, classSessionFields);

            Map<String, Object> sessionContext = sessionAdapter
                    .getSessionContext();
            sessionContext.putAll(classSessionFieldValues);

        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("No SessionFields found for class "
                        + action.getClass());
            }
        }
    }

}
