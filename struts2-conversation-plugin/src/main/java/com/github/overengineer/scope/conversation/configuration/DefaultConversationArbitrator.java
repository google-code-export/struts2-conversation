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
 *  $Id: DefaultConversationArbitrator.java reesbyars $
 ******************************************************************************/
package com.github.overengineer.scope.conversation.configuration;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.github.overengineer.scope.conversation.ConversationConstants;
import com.github.overengineer.scope.conversation.annotations.BeginConversation;
import com.github.overengineer.scope.conversation.annotations.ConversationAction;
import com.github.overengineer.scope.conversation.annotations.ConversationController;
import com.github.overengineer.scope.conversation.annotations.ConversationField;
import com.github.overengineer.scope.conversation.annotations.EndConversation;
import com.github.overengineer.scope.struts2.ActionUtil;
import com.github.overengineer.scope.util.NamingUtil;
import com.github.overengineer.scope.util.ReflectionUtil;

/**
 * The default implementation of {@link ConversationArbitrator}
 * 
 * @author rees.byars
 */
public class DefaultConversationArbitrator implements ConversationArbitrator {

    private static final long serialVersionUID = -1577464106543589370L;
    protected String actionSuffix = ConversationConstants.DEFAULT_CONTROLLER_SUFFIX;

    /**
     * {@inheritDoc}
     */
    @Override
    public void setActionSuffix(String suffix) {
        this.actionSuffix = suffix;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<Field> getCandidateConversationFields(Class<?> clazz) {
        Set<Field> conversationFields = new HashSet<Field>();
        for (Field field : ReflectionUtil.getFields(clazz)) {
            if (field.isAnnotationPresent(ConversationField.class)) {
                conversationFields.add(field);
            }
        }
        return conversationFields;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<Method> getCandidateConversationMethods(Class<?> clazz) {
        Set<Method> conversationMethods = new HashSet<Method>();
        for (Method method : ReflectionUtil.getMethods(clazz)) {
            if (method.isAnnotationPresent(ConversationAction.class)) {
                conversationMethods.add(method);
            } else if (isConversationController(clazz) && isAction(method)) {
                conversationMethods.add(method);
            }
        }
        return conversationMethods;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<String> getConversations(Class<?> clazz, Field field) {
        Set<String> fieldConversations = new HashSet<String>();
        if (field.isAnnotationPresent(ConversationField.class)) {
            ConversationField conversationField = field
                    .getAnnotation(ConversationField.class);
            String[] conversations = conversationField.conversations();
            if (conversations.length == 0) {
                conversations = getConversationsWithInheritance(clazz,
                        actionSuffix);
            }
            fieldConversations.addAll(Arrays.asList(conversations));
        }
        return fieldConversations;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<String> getConversations(Class<?> clazz, Method method) {
        Set<String> methodConversations = new HashSet<String>();
        if (method.isAnnotationPresent(ConversationAction.class)) {
            ConversationAction conversationmethod = method.getAnnotation(ConversationAction.class);
            String[] conversations = conversationmethod.conversations();
            if (conversations.length == 0) {
                conversations = getConversationsWithInheritance(clazz, actionSuffix);
            }
            methodConversations.addAll(Arrays.asList(conversations));
        } else {
            methodConversations.addAll(Arrays.asList(getConversationsWithInheritance(clazz, actionSuffix)));
        }
        return methodConversations;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName(Field field) {
        String name = field.getName();
        if (field.isAnnotationPresent(ConversationField.class)) {
            ConversationField conversationField = field.getAnnotation(ConversationField.class);
            String annotationName = conversationField.name();
            if (!annotationName.equals(ConversationField.DEFAULT)) {
                name = annotationName;
            }
        }
        return name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName(Method method) {
        return method.getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<String> getBeginConversations(Class<?> clazz, Method method) {
        Set<String> methodConversations = new HashSet<String>();
        Class<?> declaringClass = method.getDeclaringClass();
        if (method.isAnnotationPresent(BeginConversation.class)) {
            BeginConversation conversationmethod = method.getAnnotation(BeginConversation.class);
            methodConversations.addAll(Arrays.asList(conversationmethod.conversations()));
            if (declaringClass.equals(clazz) && methodConversations.size() == 0) {
                methodConversations.addAll(Arrays.asList(getConversationsWithoutInheritance(clazz, actionSuffix)));
            }
        } else if (declaringClass.equals(clazz) && method.getName().startsWith("begin")) {
            methodConversations.addAll(Arrays.asList(getConversationsWithoutInheritance(clazz, actionSuffix)));
        } else if (!declaringClass.equals(clazz)) {
            methodConversations.addAll(getBeginConversations(declaringClass, method));
        }
        return methodConversations;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<String> getEndConversations(Class<?> clazz, Method method) {
        Set<String> methodConversations = new HashSet<String>();
        Class<?> declaringClass = method.getDeclaringClass();
        if (method.isAnnotationPresent(EndConversation.class)) {
            EndConversation conversationmethod = method.getAnnotation(EndConversation.class);
            methodConversations.addAll(Arrays.asList(conversationmethod.conversations()));
            if (declaringClass.equals(clazz) && methodConversations.size() == 0) {
                methodConversations.addAll(Arrays.asList(getConversationsWithoutInheritance(clazz, actionSuffix)));
            }
        } else if (declaringClass.equals(clazz) && method.getName().startsWith("end")) {
            methodConversations.addAll(Arrays.asList(getConversationsWithoutInheritance(clazz, actionSuffix)));
        } else if (!declaringClass.equals(clazz)) {
            methodConversations.addAll(getEndConversations(declaringClass, method));
        }
        return methodConversations;
    }

    public Collection<String> getConversations(Class<?> clazz) {
        return getConversations(clazz, ConversationConstants.DEFAULT_CONTROLLER_SUFFIX);
    }

    public Collection<String> getConversations(Class<?> clazz, String actionSuffix) {
        Set<String> conversations = new HashSet<String>();
        for (Method method : ReflectionUtil.getMethods(clazz)) {
            conversations.addAll(getConversations(clazz, method, actionSuffix));
        }
        return conversations;
    }

    public Collection<String> getConversations(Class<?> clazz, Method method, String actionSuffix) {
        Set<String> methodConversations = new HashSet<String>();
        if (method.isAnnotationPresent(ConversationAction.class)) {
            ConversationAction conversationmethod = method.getAnnotation(ConversationAction.class);
            String[] conversations = conversationmethod.conversations();
            if (conversations.length == 0) {
                conversations = getConversationsWithInheritance(clazz, actionSuffix);
            }
            methodConversations.addAll(Arrays.asList(conversations));
        } else if (isAction(method)) {
            methodConversations.addAll(Arrays.asList(getConversationsWithInheritance(clazz, actionSuffix)));
        }
        return methodConversations;
    }

    protected String[] getConversationsWithInheritance(Class<?> clazz, String actionSuffix) {
        List<String> conversations = new ArrayList<String>();
        for (Class<?> conversationControllerClass : getConversationControllers(clazz)) {
            conversations.addAll(Arrays.asList(this.getConversationsWithoutInheritance(conversationControllerClass, actionSuffix)));
        }
        return conversations.toArray(new String[] {});
    }

    protected String[] getConversationsWithoutInheritance(Class<?> clazz, String actionSuffix) {
        List<String> conversations = new ArrayList<String>();
        if (clazz.isAnnotationPresent(ConversationController.class)) {
            ConversationController controller = clazz.getAnnotation(ConversationController.class);
            String[] newConversations = controller.conversations();
            if (controller.value().equals(ConversationController.DEFAULT_VALUE)) {
                if (newConversations.length == 0) {
                    newConversations = new String[] { NamingUtil.getConventionName(clazz, actionSuffix) };
                }
            } else {
                conversations.add(controller.value());
            }
            conversations.addAll(Arrays.asList(newConversations));
        }
        return conversations.toArray(new String[] {});
    }

    protected Set<Class<?>> getConversationControllers(Class<?> clazz) {
        Set<Class<?>> annotatedClasses = new HashSet<Class<?>>();
        for (Class<?> clazzClass : clazz.getInterfaces()) {
            if (clazzClass.isAnnotationPresent(ConversationController.class)) {
                annotatedClasses.add(clazzClass);
            }
        }
        if (clazz.isAnnotationPresent(ConversationController.class)) {
            annotatedClasses.add(clazz);
        }
        Class<?> superClass = clazz.getSuperclass();
        if (superClass != null) {
            annotatedClasses.addAll(getConversationControllers(superClass));
        }
        return annotatedClasses;
    }

    protected boolean isAction(Method method) {
        return ActionUtil.isAction(method);
    }

    protected boolean isConversationController(Class<?> clazz) {
        return getConversationControllers(clazz).size() > 0;
    }

}
