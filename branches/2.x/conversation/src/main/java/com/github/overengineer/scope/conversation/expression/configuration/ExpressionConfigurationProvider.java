package com.github.overengineer.scope.conversation.expression.configuration;

import java.io.Serializable;

import com.github.overengineer.scope.ActionProvider;
import com.github.overengineer.scope.container.PostConstructable;
import com.github.overengineer.scope.conversation.configuration.ConversationArbitrator;

public interface ExpressionConfigurationProvider extends Serializable, PostConstructable {
	
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
     * Set the {@link ActionProvider} for building action configurations on startup
     * 
     * @param actionProvider
     */
    public void setActionProvider(ActionProvider actionProvider);

}
