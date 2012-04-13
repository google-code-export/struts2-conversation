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
 *  $Id: StrutsConversationArbitrator.java reesbyars $
 ******************************************************************************/
package com.google.code.rees.scope.struts2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.code.rees.scope.ActionProvider;
import com.google.code.rees.scope.conversation.ConversationArbitrator;
import com.google.code.rees.scope.conversation.DefaultConversationArbitrator;
import com.google.code.rees.scope.conversation.annotations.ConversationController;
import com.google.code.rees.scope.util.NamingUtil;
import com.opensymphony.xwork2.inject.Inject;

/**
 * The {@link ConversationArbitrator} for use with Struts2.
 * 
 * @author rees.byars
 * 
 */
public class StrutsConversationArbitrator extends DefaultConversationArbitrator {

    private static final long serialVersionUID = 6842124082407418415L;

    private static final Logger LOG = LoggerFactory
            .getLogger(StrutsConversationArbitrator.class);

    private Map<Class<?>, Collection<String>> packageBasedConversations = Collections
            .synchronizedMap(new HashMap<Class<?>, Collection<String>>());

    protected boolean usePackageNesting;
    protected ActionProvider actionProvider;

    @Inject(StrutsScopeConstants.CONVERSATION_PACKAGE_NESTING_KEY)
    public void setUsePackageNesting(String usePackageNesting) {
        this.usePackageNesting = "true".equals(usePackageNesting);
    }

    @Inject(StrutsScopeConstants.ACTION_FINDER_KEY)
    public void setActionProvider(ActionProvider actionProvider) {
        this.actionProvider = actionProvider;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String[] getConversationsWithInheritance(Class<?> clazz,
            String actionSuffix) {
        List<String> conversations = new ArrayList<String>();
        conversations.addAll(Arrays.asList(super
                .getConversationsWithInheritance(clazz, actionSuffix)));
        if (this.usePackageNesting) {
            synchronized (this.packageBasedConversations) {
                Collection<String> packageConversations = this.packageBasedConversations
                        .get(clazz);
                if (packageConversations == null) {
                    packageConversations = this.getPackageBasedConversations(
                            clazz, actionSuffix);
                    this.packageBasedConversations.put(clazz,
                            packageConversations);
                }
                conversations.addAll(packageConversations);
            }
        }
        return conversations.toArray(new String[] {});
    }

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

    protected Collection<String> getPackageBasedConversations(Class<?> clazz,
            String actionSuffix) {

        String className = clazz.getName();

        LOG.debug("Getting package-based conversations for " + className);

        String classPackageString = className.substring(0,
                className.lastIndexOf("."));

        Collection<String> packageBasedConversations = new HashSet<String>();

        for (Class<?> superCandidate : this.actionProvider.getActionClasses()) {
            String superCandidateClassName = superCandidate.getName();
            String superCandidateClassPackageString = superCandidateClassName
                    .substring(0, superCandidateClassName.lastIndexOf("."));
            if (classPackageString.contains(superCandidateClassPackageString)
                    && !classPackageString
                            .equals(superCandidateClassPackageString)) {
                LOG.debug("Adding conversations from "
                        + superCandidateClassName);

                String[] superCandidateConversations = this
                        .getConversationsWithoutInheritance(superCandidate,
                                actionSuffix);
                packageBasedConversations.addAll(Arrays
                        .asList(superCandidateConversations));
            }
        }

        LOG.debug("Package-based conversations found for " + className + ":  "
                + packageBasedConversations.toString());

        return packageBasedConversations;

    }

}
