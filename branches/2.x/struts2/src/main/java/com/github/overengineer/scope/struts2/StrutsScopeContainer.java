package com.github.overengineer.scope.struts2;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.overengineer.scope.ActionProvider;
import com.github.overengineer.scope.ScopeContainer;
import com.github.overengineer.scope.conversation.ConversationProperties;
import com.github.overengineer.scope.conversation.configuration.ConversationArbitrator;
import com.github.overengineer.scope.conversation.configuration.ConversationConfigurationProvider;
import com.github.overengineer.scope.conversation.context.ConversationContextFactory;
import com.github.overengineer.scope.conversation.context.HttpConversationContextManagerProvider;
import com.github.overengineer.scope.conversation.processing.ConversationProcessor;
import com.github.overengineer.scope.conversation.expression.Eval;
import com.github.overengineer.scope.session.SessionConfigurationProvider;
import com.github.overengineer.scope.session.SessionManager;
import com.github.overengineer.scope.struts2.StrutsScopeConstants.TypeKeys;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.Inject;

public class StrutsScopeContainer implements ScopeContainer {

	private static final long serialVersionUID = -6820777796732236492L;
	private static final Logger LOG = LoggerFactory.getLogger(StrutsScopeContainer.class);
	
	private Boolean javaConfigCompleted = false;
	private Map<Class<?>, String> typeKeys  = new HashMap<Class<?>, String>();
	private Container container;
	
	@Inject(TypeKeys.ACTION_PROVIDER)
	public void setActionProviderKey(String key) {
		typeKeys.put(ActionProvider.class, key);
	}
	
	@Inject(TypeKeys.CONVERSATION_ARBITRATOR)
	public void setConversationArbitratorKey(String key) {
		typeKeys.put(ConversationArbitrator.class, key);
	}
	
	@Inject(TypeKeys.CONVERSATION_CONFIG_PROVIDER)
	public void setConversationConfigurationProviderKey(String key) {
		typeKeys.put(ConversationConfigurationProvider.class, key);
	}
	
	@Inject(TypeKeys.CONVERSATION_CONTEXT_FACTORY)
	public void setConversationContextFactoryKey(String key) {
		typeKeys.put(ConversationContextFactory.class, key);
	}
	
	@Inject(TypeKeys.CONVERSATION_CONTEXT_MANAGER_PROVIDER)
	public void setConversationContextManagerProviderKey(String key) {
		typeKeys.put(HttpConversationContextManagerProvider.class, key);
	}
	
	@Inject(TypeKeys.CONVERSATION_PROCESSOR)
	public void setConversationProcessorKey(String key) {
		typeKeys.put(ConversationProcessor.class, key);
	}
	
	@Inject(TypeKeys.CONVERSATION_PROPERTIES)
	public void setConversationPropertiesKey(String key) {
		typeKeys.put(ConversationProperties.class, key);
	}
	
	@Inject(TypeKeys.SESSION_CONFIG_PROVIDER)
	public void setSessionConfigurationProviderKey(String key) {
		typeKeys.put(SessionConfigurationProvider.class, key);
	}
	
	@Inject(TypeKeys.SESSION_MANAGER)
	public void setSessionManagerKey(String key) {
		typeKeys.put(SessionManager.class, key);
	}
	
	@Inject(TypeKeys.EXPRESSION_EVAL)
	public void setExpressionEvalKey(String key) {
		typeKeys.put(Eval.class, key);
	}
	
	@Inject
	public void setContainer(Container container) {
		this.container = container;
	}

	@Override
	public <T> T getComponent(Class<T> clazz) {
		if (!javaConfigCompleted) {
			this.executeJavaConfiguration();
		}
		return container.getInstance(clazz, typeKeys.get(clazz));
	}
	
	/**
	 * 
	 * In the absence of a more robust container than xwork's, we use this class to do configuration via code
	 *
	 */
	protected void executeJavaConfiguration() {
		
		synchronized(javaConfigCompleted) {
			
			if (!javaConfigCompleted) {
				
				javaConfigCompleted = true;
				
				/*
				 * conversation config:
				 */
				ConversationProperties properties = this.getComponent(ConversationProperties.class);
				
				ConversationArbitrator arbitrator = this.getComponent(ConversationArbitrator.class);
		        arbitrator.setActionSuffix(properties.getActionSuffix());
		        
		        ConversationConfigurationProvider configurationProvider = this.getComponent(ConversationConfigurationProvider.class);
		        configurationProvider.setArbitrator(arbitrator);
		        configurationProvider.setDefaultMaxIdleTime(properties.getMaxIdleTime());
		        configurationProvider.setDefaultMaxInstances(properties.getMaxInstances());
		        
		        ActionProvider actionProvider = this.getComponent(ActionProvider.class);
		        try {
					configurationProvider.init(actionProvider.getActionClasses());
				} catch (Exception e) {
					LOG.warn(e.getMessage());
				}
		        
		        Eval eval = this.getComponent(Eval.class);
		        
		        ConversationProcessor processor = this.getComponent(ConversationProcessor.class);
		        processor.setConfigurationProvider(configurationProvider);
		        processor.setEval(eval);
		        
		        HttpConversationContextManagerProvider contextManagerProvider = this.getComponent(HttpConversationContextManagerProvider.class);
		        ConversationContextFactory contextFactory = this.getComponent(ConversationContextFactory.class);
		        contextManagerProvider.setConversationContextFactory(contextFactory);
		        contextManagerProvider.setMonitoringFrequency(properties.getMonitoringFrequency());
		        contextManagerProvider.setMonitoringThreadPoolSize(properties.getMonitoringThreadPoolSize());
		        contextManagerProvider.init();
		        
		        /*
		         * session config:
		         */
		        SessionConfigurationProvider sessionConfigurationProvider = this.getComponent(SessionConfigurationProvider.class);
		    	ActionProvider finder = this.getComponent(ActionProvider.class);
		    	SessionManager sessionManager = this.getComponent(SessionManager.class);

		        try {
					sessionConfigurationProvider.init(finder.getActionClasses());
				} catch (Exception e) {
					LOG.warn(e.getMessage());
				}
		        
		        sessionManager.setConfigurationProvider(sessionConfigurationProvider);
			}
		}
	}

}
