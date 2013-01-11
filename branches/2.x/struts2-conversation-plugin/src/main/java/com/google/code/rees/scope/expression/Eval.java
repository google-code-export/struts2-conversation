package com.google.code.rees.scope.expression;

import java.util.Map;

/**
 * 
 * @author reesbyars
 *
 */
public interface Eval {
	
	/**
	 * 
	 * @param expression
	 * @param evaluationContext
	 * @param root
	 * @return
	 * @throws ExpressionEvaluationException
	 */
	public Object evaluate(String expression, Map<String, Object> evaluationContext, Object root) throws ExpressionEvaluationException ;
	
	/**
	 * 
	 * @param expression
	 * @return
	 * @throws ExpressionEvaluationException
	 */
	public Object evaluate(String expression) throws ExpressionEvaluationException ;

}
