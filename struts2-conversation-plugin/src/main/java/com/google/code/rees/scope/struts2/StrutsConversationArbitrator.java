package com.google.code.rees.scope.struts2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.code.rees.scope.conversation.ConversationArbitrator;
import com.google.code.rees.scope.conversation.DefaultConversationArbitrator;
import com.google.code.rees.scope.conversation.annotations.ConversationController;
import com.google.code.rees.scope.util.NamingUtil;

/**
 * The {@link ConversationArbitrator} for use with Struts2.
 * 
 * @author rees.byars
 * 
 */
public class StrutsConversationArbitrator extends DefaultConversationArbitrator {

    private static final long serialVersionUID = 6842124082407418415L;

    @Override
    protected String[] getConversationControllerConversations(Class<?> clazz,
            String actionSuffix) {
        List<String> conversations = new ArrayList<String>();
        for (Class<?> conversationControllerClass : getConversationControllers(clazz)) {
            ConversationController controller = conversationControllerClass
                    .getAnnotation(ConversationController.class);
            String[] newConversations = controller.conversations();
            if (controller.value().equals(ConversationController.DEFAULT_VALUE)) {
                if (newConversations.length == 0) {
                    newConversations = new String[] { NamingUtil
                            .getConventionName(conversationControllerClass,
                                    actionSuffix) };
                }
            } else {
                conversations.add(controller.value());
            }
            conversations.addAll(Arrays.asList(newConversations));
        }
        if (this.isModelDrivenConversation(clazz)) {
            conversations
                    .add(NamingUtil.getConventionName(clazz, actionSuffix));
        }
        return conversations.toArray(new String[] {});
    }

    @Override
    protected boolean isConversationController(Class<?> clazz) {
        return getConversationControllers(clazz).size() > 0
                || this.isModelDrivenConversation(clazz);
    }

    protected boolean isModelDrivenConversation(Class<?> clazz) {
        return (ModelDrivenConversationSupport.class.isAssignableFrom(clazz));
    }

}
