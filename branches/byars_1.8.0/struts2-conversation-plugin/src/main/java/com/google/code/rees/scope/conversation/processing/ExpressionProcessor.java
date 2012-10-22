package com.google.code.rees.scope.conversation.processing;

import com.google.code.rees.scope.conversation.ConversationAdapter;
import com.google.code.rees.scope.conversation.configuration.ConversationClassConfiguration;
import com.google.code.rees.scope.expression.Eval;

public class ExpressionProcessor implements PreActionProcessor, PostActionProcessor, PostViewProcessor {

	private static final long serialVersionUID = -8370470454038531022L;
	
	private final Eval eval;
	private final String expression;
	
	public ExpressionProcessor(Eval eval, String expression) {
		this.eval = eval;
		this.expression = expression;
	}

	@Override
	public void postProcessConversation(ConversationAdapter conversationAdapter, ConversationClassConfiguration conversationConfig, String conversationId) {
		this.eval.evaluate(expression, conversationAdapter.getActionContext(), conversationAdapter.getAction());
	}

}
