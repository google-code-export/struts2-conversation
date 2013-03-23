package com.github.overengineer.scope.conversation.expression.eval;

import java.util.Map;

import com.github.overengineer.scope.conversation.expression.exceptions.ExpressionEvaluationException;

/**
 * @author reesbyars
 */
public interface Eval {

    /**
     * @param expression
     * @param evaluationContext
     * @param root
     * @return
     * @throws ExpressionEvaluationException
     */
    public Object evaluate(String expression, Map<String, Object> evaluationContext, Object root) throws ExpressionEvaluationException;

    /**
     * @param expression
     * @return
     * @throws ExpressionEvaluationException
     */
    public Object evaluate(String expression) throws ExpressionEvaluationException;

}
