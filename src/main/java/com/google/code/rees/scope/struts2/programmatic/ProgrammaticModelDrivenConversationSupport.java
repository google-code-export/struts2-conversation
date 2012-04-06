package com.google.code.rees.scope.struts2.programmatic;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.google.code.rees.scope.ScopeAdapterFactory;
import com.google.code.rees.scope.conversation.ConversationAdapter;
import com.google.code.rees.scope.struts2.StrutsScopeConstants;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.Preparable;
import com.opensymphony.xwork2.inject.Inject;

/**
 * This class makes it simple to manage models with conversation-scoped
 * life-cycles programmatically.
 * 
 * All access to the model is through the {@link #getModel()} and
 * {@link #setModel(Serializable)} methods so that retrieval and insertion of
 * the model from and into conversation instances
 * can be managed on behalf of inheriting classes.
 * 
 * Use of this class requires zero configuration (no interceptors, etc.).
 * 
 * @author rees.byars
 * 
 * @param <T>
 */
public abstract class ProgrammaticModelDrivenConversationSupport<T extends Serializable>
        extends ActionSupport implements
        ProgrammaticModelDrivenConversation<T>, Preparable {

    private static final long serialVersionUID = -3567083451289146237L;

    private T model;
    private ScopeAdapterFactory adapterFactory;

    /**
     * Sets a {@link ScopeAdapterFactory} used to create
     * {@link ConversationAdapter ConversationAdapters}.
     * 
     * @param adapterFactory
     */
    @Inject(StrutsScopeConstants.SCOPE_ADAPTER_FACTORY_KEY)
    public void setAdapterFactory(ScopeAdapterFactory adapterFactory) {
        this.adapterFactory = adapterFactory;
    }

    /**
     * {@inheritDoc}
     * 
     * The model is scoped to the conversations indicated by
     * {@link #getConversations()}
     */
    @Override
    public T getModel() {
        if (this.model == null) {
            this.model = ProgrammaticModelDrivenConversationUtil.getModel(this,
                    this.getModelName());
        }
        return this.model;
    }

    /**
     * {@inheritDoc}
     * 
     * The model is scoped to the conversations indicated by
     * {@link #getConversations()}
     */
    @Override
    public void setModel(T model) {
        ProgrammaticModelDrivenConversationUtil.setModel(model, this,
                this.getModelName());
        this.model = model;
    }

    /**
     * The name of the model used to identify it in the
     * {@link com.google.code.rees.scope.conversation.context.ConversationContext
     * ConversationContext}.
     * 
     * This can be overridden to provide the name of choice. The default is
     * <code>this.getClass().getName()</code>.
     * 
     * @return
     */
    protected String getModelName() {
        return this.getClass().getName();
    }

    /**
     * {@inheritDoc}
     */
    public void prepare() {
        this.adapterFactory.createConversationAdapter();
        Map<String, Map<String, String>> stackItem = new HashMap<String, Map<String, String>>();
        stackItem.put(StrutsScopeConstants.CONVERSATION_ID_MAP_STACK_KEY,
                ConversationAdapter.getAdapter().getViewContext());
        ActionContext.getContext().getValueStack().push(stackItem);
    }

    /**
     * Begins new instances of this class's conversations
     */
    protected void beginConversations() {
        ProgrammaticModelDrivenConversationUtil.begin(this);
    }

    /**
     * Continues this class's conversations associated with the current request
     */
    protected void continueConversations() {
        ProgrammaticModelDrivenConversationUtil.persist(this);
    }

    /**
     * Ends this class's conversations associated with the current request
     */
    protected void endConversations() {
        ProgrammaticModelDrivenConversationUtil.end(this);
    }

}
