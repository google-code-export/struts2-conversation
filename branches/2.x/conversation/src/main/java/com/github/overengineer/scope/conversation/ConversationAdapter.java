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
 *  $Id: ConversationAdapter.java reesbyars $
 ******************************************************************************/
package com.github.overengineer.scope.conversation;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import com.github.overengineer.scope.conversation.configuration.ConversationClassConfiguration;
import com.github.overengineer.scope.conversation.configuration.DefaultConversationArbitrator;
import com.github.overengineer.scope.conversation.context.ConversationContext;
import com.github.overengineer.scope.conversation.processing.DefaultPostProcessorWrapperFactory;
import com.github.overengineer.scope.conversation.processing.PostActionProcessor;
import com.github.overengineer.scope.conversation.processing.PostProcessorWrapper;
import com.github.overengineer.scope.conversation.processing.PostProcessorWrapperFactory;
import com.github.overengineer.scope.conversation.processing.PostViewProcessor;
import com.github.overengineer.scope.conversation.processing.PreActionProcessor;

/**
 * This class is used to adapt/integrate the major components of the
 * conversation management to control frameworks such as Struts2 and Spring MVC
 * or any other framework.
 * <p/>
 * By employing the adapter pattern, the conversation management aspects are
 * separated from the details of the control framework and separated even from
 * the underlying request and session mechanisms.
 * <p/>
 * Makes use of {@link ThreadLocal} to make the current request's adapter
 * available through the static call
 * <code>ConversationAdapter.<i>getAdapter()</i></code>.
 *
 * @author rees.byars
 */
public abstract class ConversationAdapter implements Serializable {

    private static final long serialVersionUID = -8006640931436858515L;
    protected static ThreadLocal<ConversationAdapter> conversationAdapter = new ThreadLocal<ConversationAdapter>();
    protected Map<String, String> viewContext = new HashMap<String, String>();
    protected PostProcessorWrapperFactory postProcessorFactory = new DefaultPostProcessorWrapperFactory();
    protected Collection<PostProcessorWrapper<PreActionProcessor>> preActionProcessors = new HashSet<PostProcessorWrapper<PreActionProcessor>>();
    protected Collection<PostProcessorWrapper<PostActionProcessor>> postActionProcessors = new HashSet<PostProcessorWrapper<PostActionProcessor>>();
    protected Collection<PostProcessorWrapper<PostViewProcessor>> postViewProcessors = new HashSet<PostProcessorWrapper<PostViewProcessor>>();

    public ConversationAdapter() {
        conversationAdapter.set(this);
    }

    /**
     * The controller instance, such as a Struts2 action class or a Spring MVC
     * controller
     *
     * @return
     */
    public abstract Object getAction();

    /**
     * A string identifying the current action. The convention employed by the
     * {@link DefaultConversationArbitrator} is the name of the controller
     * method being executed.
     *
     * @return
     */
    public abstract String getActionId();

    /**
     * The context for the action execution (models, etc.).  Guaranteed to have the active conversation contexts placed in it prior to action execution.
     * This responsibility lies with the ConversationProcessor.
     *
     * @return
     */
    public abstract Map<String, Object> getActionContext();

    /**
     * Returns a map containing, at a minimum, conversation name/id key/value
     * pairs for the current request.
     *
     * @return
     */
    public abstract Map<String, String> getRequestContext();

    /**
     * begins a new conversation, returning the context for the new conversation
     *
     * @param conversationName
     * @param maxIdleTimeMillis
     * @param maxInstances
     * @return
     */
    public abstract ConversationContext beginConversation(String conversationName, long maxIdleTimeMillis, int maxInstances);

    /**
     * Returns a ConversationContext for the given name and ID
     *
     * @param conversationId
     * @return
     */
    public abstract ConversationContext getConversationContext(String conversationName, String conversationId);

    /**
     * Removes the conversation from the session, returning the context
     */
    public abstract ConversationContext endConversation(String conversationName, String conversationId);

    /**
     * Returns a map that is used to place conversation name/id key/value pairs
     * for placing in the view context (the view context being, for instance, a
     * JSP).
     *
     * @return
     */
    public Map<String, String> getViewContext() {
        return this.viewContext;
    }

    /**
     * Add a {@link PreActionProcessor} that is guaranteed to be executed
     * after action execution by a call to {@link #executePreActionProcessors()}
     *
     * @param postProcessor
     * @param conversationConfig
     * @param conversationId
     */
    public void addPreActionProcessor(PreActionProcessor postProcessor, ConversationClassConfiguration conversationConfig, String conversationId) {
        PostProcessorWrapper<PreActionProcessor> wrapper = this.postProcessorFactory.create(this, postProcessor, conversationConfig, conversationId);
        this.preActionProcessors.add(wrapper);
    }

    /**
     * Executes all {@link PreActionProcessor PreActionProcessors}
     * that have been added using
     * {@link #addPreActionProcessor(PreActionProcessor, ConversationClassConfiguration, String)}
     */
    public void executePreActionProcessors() {
        for (PostProcessorWrapper<PreActionProcessor> postProcessor : this.preActionProcessors) {
            postProcessor.postProcessConversation();
        }
    }

    /**
     * Add a {@link PostActionProcessor} that is guaranteed to be executed
     * after action execution by a call to {@link #executePostActionProcessors()}
     *
     * @param postProcessor
     * @param conversationConfig
     * @param conversationId
     */
    public void addPostActionProcessor(PostActionProcessor postProcessor, ConversationClassConfiguration conversationConfig, String conversationId) {
        PostProcessorWrapper<PostActionProcessor> wrapper = this.postProcessorFactory.create(this, postProcessor, conversationConfig, conversationId);
        this.postActionProcessors.add(wrapper);
    }

    /**
     * Executes all {@link PostActionProcessor PostActionProcessors}
     * that have been added using
     * {@link #addPostActionProcessor(PostActionProcessor, ConversationClassConfiguration, String)}
     */
    public void executePostActionProcessors() {
        for (PostProcessorWrapper<PostActionProcessor> postProcessor : this.postActionProcessors) {
            postProcessor.postProcessConversation();
        }
    }

    /**
     * Add a {@link PostViewProcessor} that is guaranteed to be executed
     * after action execution by a call to {@link #executePostViewProcessors()}
     *
     * @param postProcessor
     * @param conversationConfig
     * @param conversationId
     */
    public void addPostViewProcessor(PostViewProcessor postProcessor, ConversationClassConfiguration conversationConfig, String conversationId) {
        PostProcessorWrapper<PostViewProcessor> wrapper = this.postProcessorFactory.create(this, postProcessor, conversationConfig, conversationId);
        this.postViewProcessors.add(wrapper);
    }

    /**
     * Executes all {@link PostActionProcessor PostActionProcessors}
     * that have been added using
     * {@link #addPostActionProcessor(PostActionProcessor, ConversationClassConfiguration, String)}
     */
    public void executePostViewProcessors() {
        for (PostProcessorWrapper<PostViewProcessor> postProcessor : this.postViewProcessors) {
            postProcessor.postProcessConversation();
        }
    }

    /**
     * Set the {@link ThreadLocal} ConversationAdapter for use with the current
     * request. Called in the constructor to force new instances into the
     * ThreadLocal.
     *
     * @param adapter
     */
    public static void setAdapter(ConversationAdapter adapter) {
        conversationAdapter.set(adapter);
    }

    /**
     * Get the {@link ThreadLocal} ConversationAdapter associated with the
     * current request
     *
     * @return
     */
    public static ConversationAdapter getAdapter() {
        return conversationAdapter.get();
    }

    /**
     * clean up this resource once processing is completed for the request, static to guarantee that it references current thread's instance
     */
    public static void cleanup() {
        ConversationAdapter adapter = conversationAdapter.get();
        conversationAdapter.remove();
        if (adapter != null) {
            adapter.doCleanup();
        }
    }

    /**
     * called by {@link #cleanup()}, can be overridden to tweak
     */
    protected void doCleanup() {
    }

}
