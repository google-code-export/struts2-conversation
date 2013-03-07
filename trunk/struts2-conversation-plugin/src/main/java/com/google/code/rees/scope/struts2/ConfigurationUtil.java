package com.google.code.rees.scope.struts2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.code.rees.scope.ActionProvider;
import com.google.code.rees.scope.ScopeContainer;
import com.google.code.rees.scope.conversation.ConversationProperties;
import com.google.code.rees.scope.conversation.configuration.ConversationArbitrator;
import com.google.code.rees.scope.conversation.configuration.ConversationConfigurationProvider;
import com.google.code.rees.scope.conversation.context.ConversationContextFactory;
import com.google.code.rees.scope.conversation.context.HttpConversationContextManagerProvider;
import com.google.code.rees.scope.conversation.processing.ConversationProcessor;

/**
 * 
 * In the absence of a more robust container than xwork's, we use this class to do configuration via code
 * 
 * @author reesbyars
 *
 */
public final class ConfigurationUtil {
	
	private static final Logger LOG = LoggerFactory.getLogger(ConfigurationUtil.class);
	private static Boolean conversationConfigCompleted = false;
	
	public static void doConversationConfiguration(ScopeContainer container) {
		
		synchronized(conversationConfigCompleted) {
			
			if (!conversationConfigCompleted) {
				
				ConversationProperties properties = container.getComponent(ConversationProperties.class);
				
				ConversationArbitrator arbitrator = container.getComponent(ConversationArbitrator.class);
		        arbitrator.setActionSuffix(properties.getActionSuffix());
		        
		        ConversationConfigurationProvider configurationProvider = container.getComponent(ConversationConfigurationProvider.class);
		        configurationProvider.setArbitrator(arbitrator);
		        configurationProvider.setDefaultMaxIdleTime(properties.getMaxIdleTime());
		        
		        ActionProvider actionProvider = container.getComponent(ActionProvider.class);
		        try {
					configurationProvider.init(actionProvider.getActionClasses());
				} catch (Exception e) {
					LOG.warn(e.getMessage());
				}
		        
		        ConversationProcessor processor = container.getComponent(ConversationProcessor.class);
		        processor.setConfigurationProvider(configurationProvider);
		        
		        HttpConversationContextManagerProvider contextManagerProvider = container.getComponent(HttpConversationContextManagerProvider.class);
		        ConversationContextFactory contextFactory = container.getComponent(ConversationContextFactory.class);
		        contextManagerProvider.setConversationContextFactory(contextFactory);
		        contextManagerProvider.setMaxInstances(properties.getMaxInstances());
		        contextManagerProvider.setMonitoringFrequency(properties.getMonitoringFrequency());
		        contextManagerProvider.setMonitoringThreadPoolSize(properties.getMonitoringThreadPoolSize());
		        contextManagerProvider.init();
		        
			}
		}
		
	}

}
