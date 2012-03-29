package com.google.code.rees.scope.struts2.programmatic;

import java.io.Serializable;

import com.google.code.rees.scope.conversation.ConversationUtil;
import com.google.code.rees.scope.conversation.context.ConversationContext;

/**
 * Utility class that uses the {@link ConversationUtil} and
 * {@link ProgrammaticModelDrivenConversation} interface
 * to assist in programmatic conversation management. Used by the
 * {@link ProgrammaticModelDrivenConversationSupport} class.
 * 
 * In most uses, actions should inherit from the
 * ProgrammaticModelDrivenConversationSupport rather than using this class
 * directly.
 * 
 * @author rees.byars
 * 
 */
public class ProgrammaticModelDrivenConversationUtil {

    public static <T extends ProgrammaticModelDrivenConversation<?>> void begin(
            T controller) {
        Object model = controller.getModel();
        for (String conversationName : controller.getConversations()) {
            ConversationContext conversationContext = ConversationUtil
                    .begin(conversationName);
            conversationContext.put(conversationName, model);
        }
    }

    public static <M extends Serializable, T extends ProgrammaticModelDrivenConversation<M>> void persist(
            T controller) {
        for (String conversationName : controller.getConversations()) {
            ConversationUtil.persist(conversationName);
        }
    }

    public static <M extends Serializable, T extends ProgrammaticModelDrivenConversation<M>> void end(
            T controller) {
        for (String conversationName : controller.getConversations()) {
            ConversationUtil.end(conversationName);
        }
    }

    @SuppressWarnings("unchecked")
    public static <M extends Serializable, T extends ProgrammaticModelDrivenConversation<M>> M getModel(
            T controller, String modelKey) {
        return (M) ConversationUtil.getField(modelKey);
    }

    public static <M extends Serializable, T extends ProgrammaticModelDrivenConversation<M>> void setModel(
            M model, T controller, String modelKey) {
        ConversationUtil.setField(modelKey, model);
    }

}
