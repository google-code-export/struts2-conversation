package com.github.overengineer.scope.conversation.expression.configuration;

public interface ExpressionConfiguration {
	
	/**
	 * 
	 * @param actionId
	 * @param preActionExpression
	 * @param postActionExpression
	 * @param postViewExpression
	 */
	public void addExpressions(String actionId, String preActionExpression, String postActionExpression, String postViewExpression);
	
	/**
     * 
     * @param acitonId
     * @return
     */
    public String getPreActionExpression(String actionId);
    
    /**
     * 
     * @param acitonId
     * @return
     */
    public String getPostActionExpression(String actionId);
    
    /**
     * 
     * @param acitonId
     * @return
     */
    public String getPostViewExpression(String actionId);

}
