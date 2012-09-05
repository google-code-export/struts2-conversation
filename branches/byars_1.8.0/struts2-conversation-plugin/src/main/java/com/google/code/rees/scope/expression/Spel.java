package com.google.code.rees.scope.expression;

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

import com.google.code.rees.scope.conversation.ConversationAdapter;
import com.google.code.rees.scope.conversation.ConversationUtil;

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
    		CONVERSATION_INITIATOR = ConversationUtil.class.getDeclaredMethod("begin", new Class[] {String.class, long.class});
    		CONVERSATION_TERMINATOR = ConversationUtil.class.getDeclaredMethod("end", new Class[] {String.class});
    		CONVERSATION_CONTINUATOR = ConversationUtil.class.getDeclaredMethod("persist", new Class[] {String.class});
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
	
	public Object evaluate(String expression, Map<String, Object> evaluationContext, Object root) {
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
	}
	
	public Object evaluate(String expression) {
		return this.evaluate(expression, ConversationAdapter.getAdapter().getActionContext(), ConversationAdapter.getAdapter().getAction());
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
