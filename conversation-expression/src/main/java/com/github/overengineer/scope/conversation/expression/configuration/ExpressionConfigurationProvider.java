package com.github.overengineer.scope.conversation.expression.configuration;

import java.io.Serializable;

public interface ExpressionConfigurationProvider extends Serializable {

    /**
     * Get the {@link ExpressionConfiguration} for
     * a given class
     *
     * @param actionClass
     * @return
     */
    public ExpressionConfiguration getExpressionConfiguration(Class<?> actionClass);

}
