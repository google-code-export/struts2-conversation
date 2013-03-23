package com.github.overengineer.scope.conversation.expression.processing;

import com.github.overengineer.scope.container.Component;
import com.github.overengineer.scope.conversation.ConversationAdapter;
import com.github.overengineer.scope.conversation.exceptions.ConversationException;
import com.github.overengineer.scope.conversation.expression.configuration.ExpressionConfiguration;
import com.github.overengineer.scope.conversation.expression.configuration.ExpressionConfigurationProvider;
import com.github.overengineer.scope.conversation.expression.eval.Eval;
import com.github.overengineer.scope.conversation.processing.InjectionConversationProcessor;

public class ExpressionConversationProcessor extends InjectionConversationProcessor {

    private static final long serialVersionUID = 6123019737025985741L;

    protected Eval eval;
    protected ExpressionConfigurationProvider expressionConfigurationProvider;

    @Component
    public void setEval(Eval eval) {
        this.eval = eval;
    }

    @Component
    public void setExpressionConfigurationProvider(ExpressionConfigurationProvider expressionConfigurationProvider) {
        this.expressionConfigurationProvider = expressionConfigurationProvider;
    }

    /**
     * {@inheritDoc}
     *
     * @throws ConversationException
     */
    @Override
    public void processConversations(ConversationAdapter conversationAdapter) throws ConversationException {

        super.processConversations(conversationAdapter);

        ExpressionConfiguration expressionConfiguration = this.expressionConfigurationProvider.getExpressionConfiguration(conversationAdapter.getAction().getClass());
        if (expressionConfiguration != null) {
            String actionId = conversationAdapter.getActionId();
            String pre = expressionConfiguration.getPreActionExpression(actionId);
            if (pre != null && !"".equals(pre)) {
                conversationAdapter.addPreActionProcessor(new ExpressionProcessor(eval, pre), null, null);
            }
            String postA = expressionConfiguration.getPostActionExpression(actionId);
            if (postA != null && !"".equals(postA)) {
                conversationAdapter.addPostActionProcessor(new ExpressionProcessor(eval, postA), null, null);
            }
            String postV = expressionConfiguration.getPostViewExpression(actionId);
            if (postV != null && !"".equals(postV)) {
                conversationAdapter.addPostViewProcessor(new ExpressionProcessor(eval, postV), null, null);
            }
        }
    }

}
