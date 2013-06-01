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
 *  $Id: ConversationInterceptor.java reesbyars $
 ******************************************************************************/
package com.github.overengineer.scope.struts2;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import com.github.overengineer.container.Provider;
import org.apache.struts2.StrutsStatics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.overengineer.scope.conversation.ConversationAdapter;
import com.github.overengineer.scope.conversation.context.ConversationContextManager;
import com.github.overengineer.scope.conversation.context.JeeConversationContextManagerProvider;
import com.github.overengineer.scope.conversation.exceptions.ConversationException;
import com.github.overengineer.scope.conversation.exceptions.ConversationIdException;
import com.github.overengineer.scope.conversation.processing.ConversationProcessor;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.interceptor.MethodFilterInterceptor;
import com.opensymphony.xwork2.interceptor.PreResultListener;
import com.opensymphony.xwork2.util.LocalizedTextUtil;
import com.opensymphony.xwork2.util.TextParseUtil;

/**
 * This interceptor uses an {@link ConversationProcessor} to process conversation states and scopes.  This
 * is the entry/exit, integration point for the conversation framework within the Struts2 framework.  If
 * something such as a {@link ConversationIdException} occurs, the interceptor short-circuits the action
 * invocation and returns either a {@link #CONVERSATION_EXCEPTION_KEY} or a {@link #CONVERSATION_ID_EXCEPTION_KEY}
 * result with the error message present on the value stack.
 *
 * @author rees.byars
 */
public class ConversationInterceptor extends MethodFilterInterceptor {

    private static final long serialVersionUID = -72776817859403642L;
    private static final Logger LOG = LoggerFactory.getLogger(ConversationInterceptor.class);

    /**
     * This key can be used in a message resource bundle to specify a message in the case of a user
     * submitting a request with an invalid conversation ID (i.e. the conversation has already ended or expired)
     * and also to map results as this will be the result value
     */
    public static final String CONVERSATION_ID_EXCEPTION_KEY = "struts.conversation.invalid.id";

    /**
     * This key can be used in a message resource bundle to specify a message in the case of a an
     * unexpected error occurring during conversation processing and also to map results as this will
     * be the result value.
     * <p/>
     * Of course, we don't expect that this will happen ;)
     */
    public static final String CONVERSATION_EXCEPTION_KEY = "struts.conversation.general.error";

    /**
     * In the case of an invalid id result, this key can be used to retrieve the offending
     * conversation's name from the {@link com.opensymphony.xwork2.util.ValueStack ValueStack}.
     * This value can then be referenced in a message with the expression ${conversation.name}
     */
    public static final String CONVERSATION_EXCEPTION_NAME_STACK_KEY = "conversation.name";

    /**
     * In the case of an invalid id result, this key can be used to retrieve the offending
     * conversation ID from the {@link com.opensymphony.xwork2.util.ValueStack ValueStack}.
     * This value can then be referenced in a message with the expression ${conversation.id}
     */
    public static final String CONVERSATION_EXCEPTION_ID_STACK_KEY = "conversation.id";

    protected JeeConversationContextManagerProvider contextManagerProvider;
    protected ConversationProcessor processor;
    protected Provider componentProvider;
    protected Set<String> shortCircuitResults = Collections.emptySet();

    public void setShortCircuitResults(String shortCircuitResults) {
        this.shortCircuitResults = TextParseUtil.commaDelimitedStringToSet(shortCircuitResults);
    }

    @Inject
    public void setMetaProvider(StrutsScopeContainerProvider metaProvider) {
        componentProvider = metaProvider.getProvider();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void destroy() {
        LOG.info("Destroying the ConversationInterceptor");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init() {

        LOG.info("Initializing the Conversation Interceptor");

        contextManagerProvider = componentProvider.get(JeeConversationContextManagerProvider.class);

        processor = componentProvider.get(ConversationProcessor.class);

        LOG.info("Conversation Interceptor successfully initialized");

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String doIntercept(ActionInvocation invocation) throws Exception {

        try {

            ActionContext actionContext = invocation.getInvocationContext();
            HttpServletRequest request = (HttpServletRequest) actionContext.get(StrutsStatics.HTTP_REQUEST);
            ConversationContextManager contextManager = contextManagerProvider.getManager(request);
            final ConversationAdapter adapter = new StrutsConversationAdapter(invocation, contextManager);

            processor.processConversations(adapter);

            invocation.addPreResultListener(new PreResultListener() {
                @Override
                public void beforeResult(ActionInvocation invocation, String resultCode) {
                    if (!ConversationInterceptor.this.shortCircuitResults.contains(resultCode)) {
                        adapter.executePostActionProcessors();
                    }
                    invocation.getStack().getContext().put(StrutsScopeConstants.CONVERSATION_ID_MAP_STACK_KEY, adapter.getViewContext());
                }
            });

            adapter.executePreActionProcessors();

            this.cleanContext(adapter, actionContext);

            String result = invocation.invoke();

            if (shortCircuitResults.contains(result)) {
                result = this.handleShortCircuitResult(result, invocation, adapter);
            } else {
                result = this.handleResult(result, invocation, adapter);
            }

            return result;

        } catch (ConversationIdException cie) {

            return this.handleIdException(invocation, cie);

        } catch (ConversationException ce) {

            return this.handleUnexpectedException(invocation, ce);

        } finally {

            ConversationAdapter.cleanup();

        }

    }

    /**
     *
     * @param adapter
     * @param actionContext
     */
    protected void cleanContext(ConversationAdapter adapter, ActionContext actionContext) {
        Map<String, Object> params = actionContext.getParameters();
        for (String paramName : adapter.getRequestContext().keySet()) {
            params.remove(paramName);
        }
    }

    /**
     * Handles logging and UI messages for ConversationIdExceptions
     *
     * @param invocation
     * @param cie
     * @return
     */
    protected String handleIdException(ActionInvocation invocation, ConversationIdException cie) {

        LOG.warn("ConversationIdException occurred in Conversation Processing, returning result of " + CONVERSATION_ID_EXCEPTION_KEY);

        Locale locale = invocation.getInvocationContext().getLocale();
        Map<String, Object> stackContext = invocation.getStack().getContext();

        //Placing exception details on stack for OGNL retrieval in messages
        stackContext.put(CONVERSATION_EXCEPTION_NAME_STACK_KEY, cie.getConversationName());
        stackContext.put(CONVERSATION_EXCEPTION_ID_STACK_KEY, cie.getConversationId());

        //message key for the conversation
        final String conversationSpecificMessageKey = CONVERSATION_ID_EXCEPTION_KEY + "." + cie.getConversationName();

        //First, we attempt to get a conversation-specific message from a bundle
        String errorMessage = LocalizedTextUtil.findText(this.getClass(), conversationSpecificMessageKey, locale);

        //If conversation specific message not found, get generic message, if that not found use default
        if (errorMessage == null || errorMessage.equals(conversationSpecificMessageKey)) {
            errorMessage = LocalizedTextUtil.findText(this.getClass(), CONVERSATION_ID_EXCEPTION_KEY, locale,
                    "The workflow that you are attempting to continue has ended or expired.  Your requested action was not processed.", new Object[0]);
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("Placing Conversation Error Message on stack (key={" + CONVERSATION_ID_EXCEPTION_KEY + "}):  " + errorMessage);
        }

        //Placing message on stack instead of in actionErrors for retrieval in UI
        stackContext.put(CONVERSATION_ID_EXCEPTION_KEY, errorMessage);

        this.handleConversationErrorAware(invocation.getProxy(), errorMessage);

        return CONVERSATION_ID_EXCEPTION_KEY;
    }

    /**
     * Handles logging and UI messages for ConversationExceptions
     *
     * @param invocation
     * @param ce
     * @return
     */
    protected String handleUnexpectedException(ActionInvocation invocation, ConversationException ce) {

        LOG.error("An unexpected exception occurred in Conversation Processing, returning result of " + CONVERSATION_EXCEPTION_KEY);

        Locale locale = invocation.getInvocationContext().getLocale();

        String errorMessage = LocalizedTextUtil.findText(this.getClass(), CONVERSATION_EXCEPTION_KEY, locale,
                "An unexpected error occurred while processing you request.  Please try again.", new Object[0]);

        if (LOG.isDebugEnabled()) {
            LOG.debug("Placing Conversation Error Message on stack (key={" + CONVERSATION_EXCEPTION_KEY + "}):  " + errorMessage);
        }

        //Placing message on stack instead of in actionErrors for retrieval in UI
        invocation.getStack().getContext().put(CONVERSATION_EXCEPTION_KEY, errorMessage);

        this.handleConversationErrorAware(invocation.getProxy(), errorMessage);

        return CONVERSATION_EXCEPTION_KEY;
    }

    /**
     * This provides extra functionality over placing on stack in that it allows for
     * easily propagating the error through a redirect using a static result param:
     * <p/>
     * <tt>&ltparam name="conversationError">${conversationError}&lt/param></tt>
     */
    protected void handleConversationErrorAware(ActionProxy proxy, String errorMessage) {

        Object action = proxy.getAction();

        if (action instanceof ConversationErrorAware) {

            LOG.debug("Action is an instance of ConversationErrorAware; setting conversation error.");

            ((ConversationErrorAware) action).setConversationError(errorMessage);

        }
    }

    protected String handleShortCircuitResult(String result, ActionInvocation invocation, ConversationAdapter adapter) {
        return result;
    }

    protected String handleResult(String result, ActionInvocation invocation, ConversationAdapter adapter) {
        adapter.executePostViewProcessors();
        return result;
    }

}
