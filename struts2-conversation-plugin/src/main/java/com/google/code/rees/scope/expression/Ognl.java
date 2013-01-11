package com.google.code.rees.scope.expression;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.code.rees.scope.conversation.ConversationAdapter;
import com.google.code.rees.scope.conversation.ConversationUtil;
import com.google.code.rees.scope.conversation.context.ConversationContext;

import ognl.OgnlException;

/**
 * 
 * @author reesbyars
 *
 */
public class Ognl implements Eval {
	
	private static final Logger LOG = LoggerFactory.getLogger(Ognl.class);
	private static final Object UTILITY_WRAPPER = new Object() {
		@SuppressWarnings("unused")
		public ConversationContext get(String name) {
			return ConversationUtil.getContextUsingSimpleName(name);
		}
		@SuppressWarnings("unused")
		public ConversationContext beg(String name, long time, int instances) {
			return ConversationUtil.beginUsingSimpleName(name, time, instances);
		}
		@SuppressWarnings("unused")
		public ConversationContext con(String name) {
			return ConversationUtil.persistUsingSimpleName(name);
		}
		@SuppressWarnings("unused")
		public ConversationContext end(String name) {
			return ConversationUtil.endUsingSimpleName(name);
		}
	};
	private final Map<String, Object> compiledExpressions = new ConcurrentHashMap<String, Object>();
	private final TemplateParser templateParser = new SimpleCachingTemplateParser();
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object evaluate(String expression, Map<String, Object> evaluationContext, Object root) throws ExpressionEvaluationException {
		
		Object result = expression;
		Object compiledExpression = null;
		evaluationContext.put("c", UTILITY_WRAPPER);
		
		try {
			compiledExpression = this.compiledExpressions.get(expression);
			if (compiledExpression == null) {
				List<String> subExpressions = this.templateParser.parse(expression);
				if (subExpressions.size() > 1) {
					StringBuilder resultBuilder = new StringBuilder();
					for (String subExpression : subExpressions) {
						compiledExpression = this.compiledExpressions.get(subExpression);
						if (compiledExpression == null) {
							compiledExpression = ognl.Ognl.parseExpression(subExpression);
							this.compiledExpressions.put(subExpression, compiledExpression);
						}
						try {
							resultBuilder.append(ognl.Ognl.getValue(compiledExpression, evaluationContext, root));
						} catch (OgnlException e) {
							LOG.error("Failed to evaluate expression [" + expression + "].  Message:  " + e.getMessage());
						}
					}
					result = resultBuilder.toString();
				} else {
					compiledExpression = ognl.Ognl.parseExpression(subExpressions.get(0));
					this.compiledExpressions.put(expression, compiledExpression);
					try {
						result = ognl.Ognl.getValue(compiledExpression, evaluationContext, root);
					} catch (OgnlException e) {
						LOG.error("Failed to evaluate expression [" + expression + "].  Message:  " + e.getMessage());
					}
				}
			} else {
				try {
					result = ognl.Ognl.getValue(compiledExpression, evaluationContext, root);
				} catch (OgnlException e) {
					LOG.error("Failed to evaluate expression [" + expression + "].  Message:  " + e.getMessage());
				}
			}
		} catch (OgnlException e) {
			LOG.error("Failed to obtain compiled object graph for [" + expression + "].  Message:  " + e.getMessage() + "  Details:  " + e.getReason());
			throw new ExpressionEvaluationException(expression, e);
		} catch (Exception e) {
			throw new ExpressionEvaluationException(expression, e);
		}
		
		return result;
	}

	/**
	 * {@inheritDoc} 
	 */
	@Override
	public Object evaluate(String expression) throws ExpressionEvaluationException {
		return evaluate(expression, ConversationAdapter.getAdapter().getActionContext(), ConversationAdapter.getAdapter().getAction());
	}
	
}
