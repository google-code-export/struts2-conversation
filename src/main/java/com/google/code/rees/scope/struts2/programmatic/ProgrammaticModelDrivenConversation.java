package com.google.code.rees.scope.struts2.programmatic;

import java.io.Serializable;

import com.opensymphony.xwork2.ModelDriven;

/**
 * An interface for simplifying the integration of the {@link ModelDriven}
 * interface
 * and pattern with the programmatic management of the conversation life-cycle.
 * 
 * In most cases, extending the ProgrammaticModelDrivenConversationSupport
 * should
 * be used in preference to directly implementing this interface.
 * 
 * @author rees.byars
 * 
 * @param <T>
 */
public interface ProgrammaticModelDrivenConversation<T extends Serializable>
        extends ModelDriven<T> {

    /**
     * Set the model instance.
     * 
     * @param model
     */
    public void setModel(T model);

    /**
     * The conversations for which this class is a member.
     * 
     * @return
     */
    public String[] getConversations();

}
