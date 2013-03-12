package com.github.overengineer.scope.conversation.expression.configuration;

import java.util.Set;

import com.github.overengineer.scope.conversation.configuration.ConversationArbitrator;

public interface ExpressionConfigurationProvider {
	
	/**
     * Get the {@link ExpressionConfiguration} for
     * a given class
     * 
     * @param actionClass
     * @return
     */
    public ExpressionConfiguration getExpressionConfiguration(Class<?> actionClass);
    
    /**
     * Set the {@link ConversationArbitrator} to be used for building the
     * configurations
     * 
     * @param arbitrator
     */
    public void setArbitrator(ConversationArbitrator arbitrator);
    
    /**
     * Initialize the configuration caches for a given set of classes
     * 
     * @param actionClasses
     */
    public void init(Set<Class<?>> actionClasses);

}
