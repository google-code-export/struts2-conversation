package com.google.code.rees.scope.expression;

import java.util.Map;

public interface Eval {
	
	public Object evaluate(String expression, Map<String, Object> evaluationContext, Object root);
	
	public Object evaluate(String expression);

}
