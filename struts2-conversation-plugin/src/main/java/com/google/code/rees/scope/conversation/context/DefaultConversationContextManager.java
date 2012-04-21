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
 *  $Id: DefaultConversationContextManager.java reesbyars $
 ******************************************************************************/
package com.google.code.rees.scope.conversation.context;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The default implementation of the {@link ConversationContextManager}.
 * 
 * @author rees.byars
 * 
 */
public class DefaultConversationContextManager implements ConversationContextManager {

    private static final long serialVersionUID = 3699451038473294837L;
    private static final Logger LOG = LoggerFactory.getLogger(DefaultConversationContextManager.class);

    protected ConversationContextFactory contextFactory;
	protected Map<String, Map<String, ConversationContext>> conversations = new HashMap<String, Map<String, ConversationContext>>();
	protected int maxInstances = DEFAULT_MAXIMUM_NUMBER_OF_A_GIVEN_CONVERSATION;
	protected long conversationDuration = DEFAULT_CONVERSATION_DURATION;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setConversationDuration(long duration) {
		this.conversationDuration = duration;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setMaxInstances(int maxInstances) {
		this.maxInstances = maxInstances;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setContextFactory(ConversationContextFactory contextFactory) {
		this.contextFactory = contextFactory;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ConversationContext getContext(String conversationName, String conversationId) {
		return this.getContext(conversationName, conversationId, this.conversationDuration);
	}
	
	@Override
	public ConversationContext getContext(String conversationName, String conversationId, long maxIdleTimeMillis) {
		
		Map<String, ConversationContext> conversationContexts = this.conversations.get(conversationName);
		ConversationContext context = null;
		if (conversationContexts == null) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("Creating new conversation cache and instance for " + conversationName);
			}
			conversationContexts = new HashMap<String, ConversationContext>();
			this.conversations.put(conversationName, conversationContexts);
			context = this.contextFactory.create(conversationName, conversationId, maxIdleTimeMillis);
			conversationContexts.put(conversationId, context);
		} else {
			if (LOG.isDebugEnabled()) {
				LOG.debug("Retrieving cached instance for conversation " + conversationName);
			}
			context = conversationContexts.get(conversationId);
			if (context == null) {
				if (LOG.isDebugEnabled()) {
					LOG.debug("No instance of " + conversationName + " found.  Creating new instance.");
				}
				context = this.contextFactory.create(conversationName, conversationId, maxIdleTimeMillis);
				conversationContexts.put(conversationId, context);
			}
			if (conversationContexts.size() > this.maxInstances) {
				if (LOG.isDebugEnabled()) {
					LOG.debug("Cached instances of conversation " + conversationName + " exceeds limit.  Removing stale conversations.");
				}
				this.removeMostStaleConversation(conversationContexts, context.getRemainingTime());
			}
		}
		context.reset();
		return context;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ConversationContext remove(String conversationName, String conversationId) {
		
		if (LOG.isDebugEnabled()) {
			LOG.debug("Discarding " + conversationName + " with ID of " + conversationId);
		}

		ConversationContext context = null;

		Map<String, ConversationContext> conversationContexts = this.conversations.get(conversationName);

		if (conversationContexts != null) {
			context = conversationContexts.remove(conversationId);
		}

		return context;

	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void destroy() {
		LOG.debug("Destroying ConversationManager and clearing conversation cache.");
		this.conversations.clear();
		LOG.debug("ConversationManager destroyed and conversation cache cleared.");
	}

	/**
	 * Recursively removes the least-recently accessed conversations until the
	 * number of remaining conversations equals {@link #maxInstances}
	 */
	protected void removeMostStaleConversation(Map<String, ConversationContext> conversationContexts, long defaultDuration) {

		String mostStaleId = null;
		long leastRemainingTime = defaultDuration;
		for (Entry<String, ConversationContext> entry : conversationContexts.entrySet()) {
			long entryRemainingTime = entry.getValue().getRemainingTime();
			if (entryRemainingTime <= leastRemainingTime) {
				mostStaleId = entry.getKey();
				leastRemainingTime = entryRemainingTime;
			}
		}
		
		ConversationContext discardedContext = conversationContexts.remove(mostStaleId);

		if (LOG.isDebugEnabled()) {
			LOG.debug("Discarding most stale " + discardedContext.getConversationName() + " context with ID " + discardedContext.getId());
			LOG.debug("Remaining " + discardedContext.getConversationName() + " contexts for this session:  " + conversationContexts.size());
		}

		if (conversationContexts.size() > this.maxInstances) {
			removeMostStaleConversation(conversationContexts, defaultDuration);
		}

	}

}
