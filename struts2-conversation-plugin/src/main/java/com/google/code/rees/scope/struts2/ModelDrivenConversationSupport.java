package com.google.code.rees.scope.struts2;

import java.io.Serializable;

import com.google.code.rees.scope.conversation.ConversationUtil;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ModelDriven;

/**
 * This class makes it simple to manage models with conversation-scoped
 * life-cycles. It can be used in place of the
 * {@link com.google.code.rees.scope.conversation.annotations.ConversationController
 * ConversationController} annotation for people who hate annotations/meta-data.
 * 
 * All access to the model is through the {@link #getModel()} and
 * {@link #setModel(Serializable)} methods so that retrieval and insertion of
 * the model from and into conversation instances
 * can be managed on behalf of inheriting classes.
 * 
 * 
 * @author rees.byars
 * 
 * @param <T>
 */
public abstract class ModelDrivenConversationSupport<T extends Serializable>
        extends ActionSupport implements ModelDriven<T> {

    public static final String DEFAULT_MODEL_KEY = "conversation.model";
    private static final long serialVersionUID = -8313905274280660299L;
    private T model;

    /**
     * {@inheritDoc}
     * 
     * The model is scoped to the conversations associated with the current
     * request and action.
     */
    @SuppressWarnings("unchecked")
    @Override
    public T getModel() {
        if (this.model == null) {
            this.model = (T) ConversationUtil.getField(this.getModelName());
        }
        return this.model;
    }

    /**
     * Set the model. The model is scoped to the conversations associated with
     * the current request and action.
     * 
     * @param model
     */
    public void setModel(T model) {
        ConversationUtil.setField(this.getModelName(), model);
        this.model = model;
    }

    /**
     * The name of the model used to identify it in the
     * {@link com.google.code.rees.scope.conversation.context.ConversationContext
     * ConversationContext}.
     * 
     * This can be overridden to provide unique names (the default is
     * {@link #DEFAULT_MODEL_KEY}).
     * 
     * This should only be necessary when the potential for collisions exists.
     * Collisions
     * occur when 2 or more ModelDrivenConversationSupport-extending
     * action classes
     * are members of the same conversation, but use different classes for their
     * models. In these
     * cases, this method should be overridden.
     * 
     * @return
     */
    protected String getModelName() {
        return DEFAULT_MODEL_KEY;
    }

}
