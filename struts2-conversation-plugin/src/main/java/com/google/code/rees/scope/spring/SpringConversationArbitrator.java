package com.google.code.rees.scope.spring;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.web.bind.annotation.RequestMapping;

import com.google.code.rees.scope.conversation.DefaultConversationArbitrator;
import com.google.code.rees.scope.conversation.annotations.ConversationController;
import com.google.code.rees.scope.util.NamingUtil;

public class SpringConversationArbitrator extends DefaultConversationArbitrator {

    private static final long serialVersionUID = -2131295964932528989L;

    @Override
    protected String[] getConversationControllerConversations(Class<?> clazz,
            String actionSuffix) {
        List<String> conversations = new ArrayList<String>();
        for (Class<?> conversationControllerClass : getConversationControllers(clazz)) {
            if (clazz.isAnnotationPresent(ConversationController.class)) {
                ConversationController controller = conversationControllerClass
                        .getAnnotation(ConversationController.class);
                String[] newConversations = controller.conversations();
                if (controller.value().equals(
                        ConversationController.DEFAULT_VALUE)) {
                    if (newConversations.length == 0) {
                        newConversations = new String[] { NamingUtil
                                .getConventionName(conversationControllerClass,
                                        actionSuffix) };
                    }
                } else {
                    conversations.add(controller.value());
                }
                conversations.addAll(Arrays.asList(newConversations));
            } else {
                com.google.code.rees.scope.spring.ConversationController controller = conversationControllerClass
                        .getAnnotation(com.google.code.rees.scope.spring.ConversationController.class);
                String[] newConversations = controller.conversations();
                if (controller
                        .value()
                        .equals(com.google.code.rees.scope.spring.ConversationController.DEFAULT_VALUE)) {
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
        }
        return conversations.toArray(new String[] {});
    }

    @Override
    protected Set<Class<?>> getConversationControllers(Class<?> clazz) {
        Set<Class<?>> annotatedClasses = new HashSet<Class<?>>();
        for (Class<?> clazzClass : clazz.getInterfaces()) {
            if (clazzClass.isAnnotationPresent(ConversationController.class)
                    || clazzClass
                            .isAnnotationPresent(com.google.code.rees.scope.spring.ConversationController.class)) {
                annotatedClasses.add(clazzClass);
            }
        }
        if (clazz.isAnnotationPresent(ConversationController.class)
                || clazz.isAnnotationPresent(com.google.code.rees.scope.spring.ConversationController.class)) {
            annotatedClasses.add(clazz);
        }
        Class<?> superClass = clazz.getSuperclass();
        if (superClass != null) {
            annotatedClasses.addAll(getConversationControllers(superClass));
        }
        return annotatedClasses;
    }

    @Override
    protected boolean isAction(Method method) {
        return method.isAnnotationPresent(RequestMapping.class);
    }
}
