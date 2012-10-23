package com.google.code.rees.scope.conversation.processing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.code.rees.scope.conversation.ConversationAdapter;
import com.google.code.rees.scope.conversation.configuration.ConversationClassConfiguration;
import com.google.code.rees.scope.expression.Eval;
import com.google.code.rees.scope.expression.ExpressionEvaluationException;

public class ExpressionProcessor implements PreActionProcessor, PostActionProcessor, PostViewProcessor {

	private static final long serialVersionUID = -8370470454038531022L;
	
	private static final Logger LOG = LoggerFactory.getLogger(ExpressionProcessor.class);
	
	private final Eval eval;
	private final String expression;
	
	public ExpressionProcessor(Eval eval, String expression) {
		this.eval = eval;
		this.expression = expression;
	}

	@Override
	public void postProcessConversation(ConversationAdapter conversationAdapter, ConversationClassConfiguration conversationConfig, String conversationId) {
		try {
			this.eval.evaluate(expression, conversationAdapter.getActionContext(), conversationAdapter.getAction());
		} catch (ExpressionEvaluationException e) {
			LOG.error(e.getMessage());
		}
	}

}
