package com.google.code.rees.scope.expression;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.mvel2.MVEL;
import org.mvel2.ParserContext;
import org.mvel2.compiler.ExpressionCompiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.code.rees.scope.conversation.ConversationAdapter;
import com.google.code.rees.scope.conversation.ConversationUtil;

/**
 * 
 * @author reesbyars
 *
 */
public class Mvel implements Eval {
	
	private static final Logger LOG = LoggerFactory.getLogger(Mvel.class);
	private final Map<String, Serializable> templateCache = new ConcurrentHashMap<String, Serializable>();
	private final ParserContext context;
	
	public Mvel() {
		context = new ParserContext();
		try {
			context.addImport("get", ConversationUtil.class.getMethod("getContextUsingSimpleName", String.class));
			context.addImport("begin", ConversationUtil.class.getMethod("beginUsingSimpleName", new Class<?>[]{String.class, long.class, int.class}));
			context.addImport("end", ConversationUtil.class.getMethod("endUsingSimpleName", String.class));
			context.addImport("continue", ConversationUtil.class.getMethod("persistUsingSimpleName", String.class));
		} catch (SecurityException e) {
			LOG.error("Could not instantiate expression parsing context.  Evaluation of @Eval expressions may be compromised.", e);
		} catch (NoSuchMethodException e) {
			LOG.error("Could not instantiate expression parsing context.  Evaluation of @Eval expressions may be compromised.", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object evaluate(String expression, Map<String, Object> evaluationContext, Object root) throws ExpressionEvaluationException {
		try {
			Serializable template = this.templateCache.get(expression);
			if (template == null) {
				LOG.debug("Compiled template not found in cache, compiling template and caching.");
				ExpressionCompiler compiler = new ExpressionCompiler(expression);
				compiler.newContext(context);       
				template = compiler.compile();
				this.templateCache.put(expression, template);
			}
			return MVEL.executeExpression(template, root, evaluationContext);
		} catch (Exception e) {
			throw new ExpressionEvaluationException(expression, e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object evaluate(String expression) throws ExpressionEvaluationException {
		try {
			return this.evaluate(expression, ConversationAdapter.getAdapter().getActionContext(), ConversationAdapter.getAdapter().getAction());
		} catch (Exception e) {
			throw new ExpressionEvaluationException(expression, e);
		}
	}
}
