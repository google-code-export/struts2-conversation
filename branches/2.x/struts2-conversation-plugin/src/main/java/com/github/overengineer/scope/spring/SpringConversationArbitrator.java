/*******************************************************************************
 * 
 *  Struts2-Conversation-Plugin - An Open Source Conversation- and Flow-Scope Solution for Struts2-based Applications
 *  =================================================================================================================
 * 
 *  Copyright (C) 2012 by Rees Byars
 *  http://code.google.com/p/struts2-conversation/
 * 
 * **********************************************************************************************************************
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 *  the License. You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 *  an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 *  specific language governing permissions and limitations under the License.
 * 
 * **********************************************************************************************************************
 * 
 *  $Id: SpringConversationArbitrator.java reesbyars $
 ******************************************************************************/
package com.github.overengineer.scope.spring;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.web.bind.annotation.RequestMapping;

import com.github.overengineer.scope.conversation.annotations.ConversationController;
import com.github.overengineer.scope.conversation.configuration.DefaultConversationArbitrator;
import com.github.overengineer.scope.util.NamingUtil;

/**
 * A {@link com.github.overengineer.scope.conversation.configuration.ConversationArbitrator
 * ConversationArbitrator} for use with Spring MVC. Looks for
 * {@link RequestMapping} annotations
 * to identify controller action methods.
 * 
 * @author rees.byars
 */
public class SpringConversationArbitrator extends DefaultConversationArbitrator {

    private static final long serialVersionUID = -2131295964932528989L;

    @Override
    protected String[] getConversationsWithInheritance(Class<?> clazz,
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
                com.github.overengineer.scope.spring.ConversationController controller = conversationControllerClass
                        .getAnnotation(com.github.overengineer.scope.spring.ConversationController.class);
                String[] newConversations = controller.conversations();
                if (controller
                        .value()
                        .equals(com.github.overengineer.scope.spring.ConversationController.DEFAULT_VALUE)) {
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
                            .isAnnotationPresent(com.github.overengineer.scope.spring.ConversationController.class)) {
                annotatedClasses.add(clazzClass);
            }
        }
        if (clazz.isAnnotationPresent(ConversationController.class)
                || clazz.isAnnotationPresent(com.github.overengineer.scope.spring.ConversationController.class)) {
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
