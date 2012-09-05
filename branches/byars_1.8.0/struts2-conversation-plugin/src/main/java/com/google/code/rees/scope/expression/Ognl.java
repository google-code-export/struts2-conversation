package com.google.code.rees.scope.expression;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.code.rees.scope.conversation.ConversationAdapter;

import ognl.OgnlException;

public class Ognl implements Eval {
	
	private static final Logger LOG = LoggerFactory.getLogger(Ognl.class);
	private final static Pattern pattern = Pattern.compile("(\\$\\{)(.*?)(\\})");
	private final Map<String, String> preparsedCache = new ConcurrentHashMap<String, String>();
	private final Map<String, Object> compiledExpressions = new ConcurrentHashMap<String, Object>();
	
	public Object evaluate(String expression, Map<String, Object> evaluationContext, Object root) {
		
		Object value = expression;
		Object compiledExpression = null;
		
		try {
			String preparsedExpression = preparse(expression);
			compiledExpression = compiledExpressions.get(preparsedExpression);
			if (compiledExpression == null) {
				compiledExpression = ognl.Ognl.parseExpression(preparsedExpression);
				compiledExpressions.put(preparsedExpression, compiledExpression);
			}
			if (LOG.isDebugEnabled()) {
				LOG.debug("Successfully obtained compiled object graph for [" + expression + "].");
			}
			try {
				value = ognl.Ognl.getValue(compiledExpression, evaluationContext, root);
				if (LOG.isDebugEnabled()) {
					LOG.debug("Successfully evaluated expression [" + expression + "].  Result:  " + value);
				}
			} catch (OgnlException e) {
				LOG.error("Failed to evaluate expression [" + expression + "].  Message:  " + e.getMessage());
			}
		} catch (OgnlException e) {
			LOG.error("Failed to obtain compiled object graph for [" + expression + "].  Message:  " + e.getMessage() + "  Details:  " + e.getReason());
		}
		
		return value;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object evaluate(String expression) {
		return evaluate(expression, ConversationAdapter.getAdapter().getActionContext(), ConversationAdapter.getAdapter().getAction());
	}
	
	/**
	 * converts the expression from the Struts2-, template-style using ${...} notation into a true OGNL expression.
	 * 
	 * @param expression
	 * @return
	 */
	private String preparse(String expression) {
    	
    	String result = null;
    	
    	result = preparsedCache.get(expression);
    	
    	if (result == null) {
    		
    		Matcher matcher = pattern.matcher(expression);

            StringBuilder expressionBuilder = new StringBuilder();
            
            int endOfLast = 0;
            while(matcher.find()) {
            	if (endOfLast > 0) {
        			expressionBuilder.append(" + ");
        		}
            	int newStart = matcher.start();
            	if (endOfLast < newStart - 1) {
            		expressionBuilder.append("'");
            		expressionBuilder.append(expression.substring(endOfLast, newStart));
            		expressionBuilder.append("' + ");
            	}
            	expressionBuilder.append("(");
            	expressionBuilder.append(matcher.group(2));
            	expressionBuilder.append(")");
            	endOfLast = matcher.end();
            }
            if (endOfLast == 0) {
            	expressionBuilder.append(expression);
            } else if (endOfLast < expression.length()) {
            	expressionBuilder.append(" + '");
            	expressionBuilder.append(expression.substring(endOfLast, expression.length()));
            	expressionBuilder.append("'");
        	}
            
            result = expressionBuilder.toString();
            
            if (LOG.isDebugEnabled()) {
            	LOG.debug("Pre-parsing of OGNL expression complete.  Was [" + expression + "] and now is [" + result + "]");
            }
            
            preparsedCache.put(expression, result);
    		
    	}
        
        return result;
	}
	
}
