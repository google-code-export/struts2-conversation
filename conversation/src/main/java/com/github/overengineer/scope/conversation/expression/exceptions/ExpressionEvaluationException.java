package com.github.overengineer.scope.conversation.expression.exceptions;

public class ExpressionEvaluationException extends Exception {

	private static final long serialVersionUID = -2444996716077717437L;
	
	public ExpressionEvaluationException(final String expression, final Exception root) {
		super("There was an error evalutating the expression [" + expression + "]", root);
	}

}
