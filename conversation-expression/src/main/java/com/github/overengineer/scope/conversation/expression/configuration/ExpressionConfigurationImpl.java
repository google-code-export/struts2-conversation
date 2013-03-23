package com.github.overengineer.scope.conversation.expression.configuration;

import java.util.HashMap;
import java.util.Map;

public class ExpressionConfigurationImpl implements ExpressionConfiguration {

    private Map<String, String> pres = new HashMap<String, String>();
    private Map<String, String> postActions = new HashMap<String, String>();
    private Map<String, String> postViews = new HashMap<String, String>();

    /**
     * {@inheritDoc}
     */
    @Override
    public void addExpressions(String actionId, String preActionExpression, String postActionExpression, String postViewExpression) {
        pres.put(actionId, preActionExpression);
        postActions.put(actionId, postActionExpression);
        postViews.put(actionId, postViewExpression);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPreActionExpression(String actionId) {
        return pres.get(actionId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPostActionExpression(String actionId) {
        return postActions.get(actionId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPostViewExpression(String actionId) {
        return postViews.get(actionId);
    }

}
