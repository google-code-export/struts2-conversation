package com.google.code.rees.scope.conversation;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.code.rees.scope.util.ScopeUtil;

/**
 * The default implementation of the {@link InjectionConversationManager}
 * 
 * @author rees.byars
 */
public class DefaultInjectionConversationManager extends
        SimpleConversationManager implements InjectionConversationManager,
        ConversationPostProcessor {

    private static final long serialVersionUID = 8632020943340087L;
    private static final Logger LOG = LoggerFactory
            .getLogger(DefaultInjectionConversationManager.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public void processConversations(ConversationAdapter adapter) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Conversation Request Context:  "
                    + adapter.getRequestContext());
        }
        super.processConversations(adapter);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void processConversation(
            ConversationConfiguration conversationConfig,
            ConversationAdapter conversationAdapter, Object action) {

        String actionId = conversationAdapter.getActionId();
        String conversationName = conversationConfig.getConversationName();
        String conversationId = conversationAdapter.getRequestContext().get(
                conversationName);

        if (LOG.isDebugEnabled()) {
            LOG.debug("Processing request for conversation " + conversationName
                    + " and action " + actionId + " of class "
                    + action.getClass());
        }

        if (conversationId != null) {

            if (conversationConfig.containsAction(actionId)) {

                Map<String, Object> conversationContext = conversationAdapter
                        .getConversationContext(conversationName,
                                conversationId);

                if (LOG.isDebugEnabled()) {
                    LOG.debug("The action is a conversation member.  Processing with context:  "
                            + conversationContext);
                }

                if (conversationContext != null) {

                    Map<String, Field> actionConversationFields = conversationConfig
                            .getFields();
                    if (actionConversationFields != null) {
                        ScopeUtil.setFieldValues(action,
                                actionConversationFields, conversationContext);
                    }
                }

                if (conversationConfig.isEndAction(actionId)) {
                    conversationAdapter.addPostProcessor(
                            new ConversationEndProcessor(), conversationConfig,
                            conversationId);
                } else {
                    conversationAdapter.addPostProcessor(this,
                            conversationConfig, conversationId);
                    conversationAdapter.getViewContext().put(conversationName,
                            conversationId);
                }
            }
        } else if (conversationConfig.isBeginAction(actionId)) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Beginning new " + conversationName
                        + " conversation.");
            }
            conversationId = ConversationUtil.generateId();
            conversationAdapter.addPostProcessor(this, conversationConfig,
                    conversationId);
            conversationAdapter.getViewContext().put(conversationName,
                    conversationId);
            conversationAdapter.getRequestContext().put(conversationName,
                    conversationId);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void postProcessConversation(
            ConversationAdapter conversationAdapter,
            ConversationConfiguration conversationConfig, String conversationId) {

        Object action = conversationAdapter.getAction();

        Map<String, Field> actionConversationFields = conversationConfig
                .getFields();

        if (actionConversationFields != null) {

            if (LOG.isDebugEnabled()) {
                LOG.debug("Getting conversation fields for Conversation "
                        + conversationConfig.getConversationName()
                        + " following execution of action "
                        + conversationAdapter.getActionId());
            }
            Map<String, Object> conversationContext = conversationAdapter
                    .getConversationContext(
                            conversationConfig.getConversationName(),
                            conversationId);
            conversationContext.putAll(ScopeUtil.getFieldValues(action,
                    actionConversationFields));

        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void injectConversationFields(Object target,
            ConversationAdapter conversationAdapter) {
        Collection<ConversationConfiguration> actionConversationConfigs = this.configurationProvider
                .getConfigurations(target.getClass());
        if (actionConversationConfigs != null) {
            for (ConversationConfiguration conversation : actionConversationConfigs) {
                String conversationName = conversation.getConversationName();
                String conversationId = conversationAdapter.getRequestContext()
                        .get(conversationName);
                if (conversationId != null) {
                    Map<String, Object> conversationContext = conversationAdapter
                            .getConversationContext(conversationName,
                                    conversationId);
                    if (conversationContext != null) {
                        Map<String, Field> actionConversationFields = conversation
                                .getFields();
                        if (actionConversationFields != null) {
                            ScopeUtil.setFieldValues(target,
                                    actionConversationFields,
                                    conversationContext);
                        }
                    }
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void extractConversationFields(Object target,
            ConversationAdapter conversationAdapter) {
        Collection<ConversationConfiguration> actionConversationConfigs = this.configurationProvider
                .getConfigurations(target.getClass());
        if (actionConversationConfigs != null) {
            for (ConversationConfiguration conversation : actionConversationConfigs) {

                Map<String, Field> actionConversationFields = conversation
                        .getFields();
                String conversationName = conversation.getConversationName();
                String conversationId = conversationAdapter.getRequestContext()
                        .get(conversationName);

                if (conversationId == null) {
                    conversationId = ConversationUtil.generateId();
                }

                if (actionConversationFields != null) {

                    Map<String, Object> conversationContext = conversationAdapter
                            .getConversationContext(conversationName,
                                    conversationId);
                    conversationContext.putAll(ScopeUtil.getFieldValues(target,
                            actionConversationFields));
                }

                conversationAdapter.getViewContext().put(conversationName,
                        conversationId);
            }
        }
    }

}
