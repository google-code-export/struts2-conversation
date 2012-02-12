package com.google.code.rees.scope.struts2;

import com.google.code.rees.scope.ScopeAdapterFactory;
import com.google.code.rees.scope.conversation.ConversationAdapter;
import com.google.code.rees.scope.session.SessionAdapter;
import com.opensymphony.xwork2.ActionContext;

/**
 * Struts2 implementation of the {@link ScopeAdapterFactory}.
 * 
 * @author rees.byars
 */
public class StrutsScopeAdapterFactory implements ScopeAdapterFactory {

    private static final long serialVersionUID = -4595690103120891078L;

    /**
     * {@inheritDoc}
     */
    @Override
    public SessionAdapter createSessionAdapter() {
        return new StrutsSessionAdapter(ActionContext.getContext()
                .getActionInvocation());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ConversationAdapter createConversationAdapter() {
        return new StrutsConversationAdapter(ActionContext.getContext()
                .getActionInvocation());
    }

}
