package com.github.overengineer.scope.struts2.config_browser;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.github.overengineer.scope.container.ScopeContainerProvider;
import com.github.overengineer.scope.conversation.ConversationConstants;
import com.github.overengineer.scope.conversation.configuration.ConversationClassConfiguration;
import com.github.overengineer.scope.conversation.configuration.ConversationConfigurationProvider;
import com.github.overengineer.scope.util.ActionUtil;
import com.github.overengineer.scope.struts2.ConversationInterceptor;
import com.github.overengineer.scope.bijection.Bijector;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.entities.InterceptorMapping;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;

public class ShowConfigAction extends org.apache.struts2.config_browser.ShowConfigAction {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(ShowConfigAction.class);
    private ConversationConfigurationProvider conversationConfigurationProvider;

    @Inject
    public void setConversationConfigurationProvider(ScopeContainerProvider scopeContainerProvider) {
        this.conversationConfigurationProvider = scopeContainerProvider.getScopeContainer().getComponent(ConversationConfigurationProvider.class);
    }

    @Override
    public String execute() throws Exception {
        String paramActionName = this.getActionName();
        if (this.getActionNames().contains(ActionConfig.WILDCARD)) {
            setActionName(ActionConfig.WILDCARD);
        }
        String result = super.execute();
        this.setActionName(paramActionName);
        return result;
    }

    @Override
    public Set<String> getActionNames() {
        String namespace = this.getNamespace();
        Set<String> actionNames = new TreeSet<String>(this.configHelper.getActionNames(this.getNamespace()));
        for (String actionName : actionNames) {
            if (ActionConfig.WILDCARD.equals(actionName)) {
                ActionConfig actionConfig = this.configHelper.getActionConfig(namespace, actionName);
                try {
                    Class<?> actionClass = Class.forName(actionConfig.getClassName());
                    actionNames.addAll(ActionUtil.getActions(actionClass));
                } catch (ClassNotFoundException e) {
                    LOG.error("Could not find action class while trying to obtain Wild Card action methods!");
                }
            }
        }
        return actionNames;
    }

    public Map<String, String> getConversations() throws ClassNotFoundException {

        ActionConfig actionConfig = this.getConfig();
        Map<String, String> conversations = new HashMap<String, String>();

        if (this.isConversationInterceptorApplied(actionConfig)) {
            Collection<ConversationClassConfiguration> realConfigs = this.getConversationConfigurations(actionConfig);
            String methodName = this.getMethodName(actionConfig);
            for (ConversationClassConfiguration realConfig : realConfigs) {
                String name = realConfig.getConversationName().replaceFirst(ConversationConstants.CONVERSATION_NAME_SUFFIX, "");
                if (realConfig.isBeginAction(methodName)) {
                    conversations.put(name, "Begin");
                } else if (realConfig.isEndAction(methodName)) {
                    conversations.put(name, "End");
                } else if (realConfig.containsAction(methodName)) {
                    conversations.put(name, "Continue");
                }
            }
        }

        return conversations;
    }

    public Map<String, String> getConversationFields() throws ClassNotFoundException {

        ActionConfig actionConfig = this.getConfig();
        Map<String, String> conversationFields = new HashMap<String, String>();

        if (this.isConversationInterceptorApplied(actionConfig)) {
            Collection<ConversationClassConfiguration> realConfigs = this.getConversationConfigurations(actionConfig);
            String methodName = this.getMethodName(actionConfig);
            for (ConversationClassConfiguration realConfig : realConfigs) {
                if (realConfig.containsAction(methodName)) {
                    Set<Bijector> bijectors = realConfig.getBijectors();
                    if (bijectors.size() > 0) {
                        StringBuilder fieldDisplayBuilder = new StringBuilder();
                        for (Bijector bijector : bijectors) {
                            fieldDisplayBuilder.append(bijector.getContextKey()).append(" (").append(bijector.getFieldType().getSimpleName()).append("), ");
                        }
                        String displayString = fieldDisplayBuilder.substring(0, fieldDisplayBuilder.length() - 2);
                        conversationFields.put(realConfig.getConversationName().replaceFirst(ConversationConstants.CONVERSATION_NAME_SUFFIX, ""), displayString);
                    }
                }
            }
        }

        return conversationFields;
    }

    protected Collection<ConversationClassConfiguration> getConversationConfigurations(ActionConfig actionConfig) throws ClassNotFoundException {
        return this.conversationConfigurationProvider.getConfigurations(Class.forName(actionConfig.getClassName()));
    }

    protected boolean isConversationInterceptorApplied(ActionConfig actionConfig) {
        boolean hasConvoInterceptor = false;
        for (InterceptorMapping interceptorMapping : actionConfig.getInterceptors()) {
            if (interceptorMapping.getInterceptor() instanceof ConversationInterceptor) {
                hasConvoInterceptor = true;
                break;
            }
        }
        return hasConvoInterceptor;
    }

    protected String getMethodName(ActionConfig actionConfig) {
        String methodName = actionConfig.getMethodName();
        if (this.getActionNames().contains(ActionConfig.WILDCARD)) {
            methodName = this.getActionName();
        }
        return methodName;
    }

}
