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

    /**
     * {@inheritDoc}
     */
    @Override
    protected String[] getConversationsWithoutInheritance(Class<?> clazz,
            String actionSuffix) {
        List<String> conversations = new ArrayList<String>(Arrays.asList(super
                .getConversationsWithoutInheritance(clazz, actionSuffix)));
        if (this.isModelDrivenConversation(clazz)
                && !clazz.isAnnotationPresent(ConversationController.class)) {
            conversations
                    .add(NamingUtil.getConventionName(clazz, actionSuffix));
        }
        return conversations.toArray(new String[] {});
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isConversationController(Class<?> clazz) {
        return getConversationControllers(clazz).size() > 0
                || this.isModelDrivenConversation(clazz);
    }

    protected boolean isModelDrivenConversation(Class<?> clazz) {
        return (ModelDrivenConversationSupport.class.isAssignableFrom(clazz));
    }

}
