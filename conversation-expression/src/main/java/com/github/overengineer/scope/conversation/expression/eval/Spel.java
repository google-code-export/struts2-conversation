package com.github.overengineer.scope.conversation.expression.eval;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.expression.Expression;
import org.springframework.expression.ParserContext;
import org.springframework.expression.spel.SpelParserConfiguration;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import com.github.overengineer.scope.conversation.ConversationAdapter;
import com.github.overengineer.scope.conversation.ConversationUtil;
import com.github.overengineer.scope.conversation.expression.exceptions.ExpressionEvaluationException;

/**
 * convenient, simple, statically-accessible singleton for evaluating SPEL expressions
 * 
 * @author reesbyars
 *
 */
public class Spel implements Eval {
	
	private static final Logger LOG = LoggerFactory.getLogger(Spel.class);
	private static Method CONVERSATION_ACCESSOR;
	private static Method CONVERSATION_INITIATOR;
	private static Method CONVERSATION_TERMINATOR;
	private static Method CONVERSATION_CONTINUATOR;
	
	static {
    	try {
    		CONVERSATION_ACCESSOR = ConversationUtil.class.getDeclaredMethod("getContextUsingSimpleName", new Class[] {String.class});
    		CONVERSATION_INITIATOR = ConversationUtil.class.getDeclaredMethod("beginUsingSimpleName", new Class[] {String.class, long.class, int.class});
    		CONVERSATION_TERMINATOR = ConversationUtil.class.getDeclaredMethod("endUsingSimpleName", new Class[] {String.class});
    		CONVERSATION_CONTINUATOR = ConversationUtil.class.getDeclaredMethod("persistUsingSimpleName", new Class[] {String.class});
		} catch (SecurityException e) {
			LOG.error("Could not instantiate Conversation Resolver properly.  Message:  " + e.getMessage());
		} catch (NoSuchMethodException e) {
			LOG.error("Could not instantiate Conversation Resolver properly.  Message:  " + e.getMessage());
		}
    }
	
	private final SpelExpressionParser parser = new SpelExpressionParser(new SpelParserConfiguration(true, true));
    private final Map<String, Expression> expressionCache = new ConcurrentHashMap<String, Expression>();
    private ParserContext parserContext = new ParserContext() {

    	@Override
		public boolean isTemplate() {
			return true;
		}

		@Override
		public String getExpressionPrefix() {
			return "${"; 
		}

		@Override
		public String getExpressionSuffix() {
			return "}";
		}
    	
    };
	
    /**
     * {@inheritDoc}
     */
	public Object evaluate(String expression, Map<String, Object> evaluationContext, Object root) throws ExpressionEvaluationException {
		try {
			Expression parsedExpression = this.expressionCache.get(expression);
			if (parsedExpression == null) {
				parsedExpression = parser.parseExpression(expression, this.parserContext);
				expressionCache.put(expression, parsedExpression);
			}
			StandardEvaluationContext context = new StandardEvaluationContext(root);
			context.setVariables(evaluationContext);
			context.registerFunction("cGet", CONVERSATION_ACCESSOR);
			context.registerFunction("cBeg", CONVERSATION_INITIATOR);
			context.registerFunction("cEnd", CONVERSATION_TERMINATOR);
			context.registerFunction("cCon", CONVERSATION_CONTINUATOR);
			return parsedExpression.getValue(context);
		} catch (Exception e) {
			throw new ExpressionEvaluationException(expression, e);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	public Object evaluate(String expression) throws ExpressionEvaluationException {
		try {
			return this.evaluate(expression, ConversationAdapter.getAdapter().getActionContext(), ConversationAdapter.getAdapter().getAction());
		} catch (Exception e) {
			throw new ExpressionEvaluationException(expression, e);
		}
	}
	
	public void setExpressionPrefix(final String prefix) {
		this.parserContext = new ParserContext() {
			
			String pre = prefix + "{"; 
			
			@Override
			public boolean isTemplate() {
				return true;
			}

			@Override
			public String getExpressionPrefix() {
				return pre; 
			}

			@Override
			public String getExpressionSuffix() {
				return "}";
			}
			
		};
	}
}
