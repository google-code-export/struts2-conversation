package com.github.overengineer.scope.conversation.expression.processing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.overengineer.scope.conversation.ConversationAdapter;
import com.github.overengineer.scope.conversation.configuration.ConversationClassConfiguration;
import com.github.overengineer.scope.conversation.expression.eval.Eval;
import com.github.overengineer.scope.conversation.expression.exceptions.ExpressionEvaluationException;
import com.github.overengineer.scope.conversation.processing.PostActionProcessor;
import com.github.overengineer.scope.conversation.processing.PostViewProcessor;
import com.github.overengineer.scope.conversation.processing.PreActionProcessor;

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
            e.printStackTrace();
        }
    }

}
