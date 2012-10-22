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
package com.google.code.rees.scope.struts2;

import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.StrutsStatics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.code.rees.scope.ActionProvider;
import com.google.code.rees.scope.conversation.ConversationAdapter;
import com.google.code.rees.scope.conversation.configuration.ConversationArbitrator;
import com.google.code.rees.scope.conversation.configuration.ConversationConfigurationProvider;
import com.google.code.rees.scope.conversation.context.ConversationContextFactory;
import com.google.code.rees.scope.conversation.context.ConversationContextManager;
import com.google.code.rees.scope.conversation.context.HttpConversationContextManagerProvider;
import com.google.code.rees.scope.conversation.exceptions.ConversationException;
import com.google.code.rees.scope.conversation.exceptions.ConversationIdException;
import com.google.code.rees.scope.conversation.processing.ConversationProcessor;
import com.google.code.rees.scope.expression.Eval;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.interceptor.MethodFilterInterceptor;
import com.opensymphony.xwork2.interceptor.PreResultListener;
import com.opensymphony.xwork2.util.LocalizedTextUtil;

/**
 * 
 * This interceptor uses an {@link ConversationProcessor} to process conversation states and scopes.  This
 * is the entry/exit, integration point for the conversation framework within the Struts2 framework.  If
 * something such as a {@link ConversationIdException} occurs, the interceptor short-circuits the action
 * invocation and returns either a {@link #CONVERSATION_EXCEPTION_KEY} or a {@link #CONVERSATION_ID_EXCEPTION_KEY}
 * result with the error message present on the value stack.  
 * 
 * @author rees.byars
 *
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
     * <p>
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

    protected Container container;
    protected ActionProvider finder;
    protected String actionSuffix;
	protected long maxIdleTime;
    protected ConversationArbitrator arbitrator;
    protected ConversationConfigurationProvider conversationConfigurationProvider;
    protected ConversationProcessor conversationProcessor;
    protected HttpConversationContextManagerProvider conversationContextManagerProvider;
    protected int monitoringThreadPoolSize;
    protected long monitoringFrequency;
    protected int maxInstances;
    protected ConversationContextFactory conversationContextFactory;
    protected Eval eval;
    private String evalProviderName;

    @Inject
    public void setContainer(Container container) {
        this.container = container;
    }
    
    @Inject(StrutsScopeConstants.ACTION_FINDER_KEY)
    public void setActionClassFinder(ActionProvider finder) {
        this.finder = finder;
    }

    @Inject(ConventionConstants.ACTION_SUFFIX)
    public void setActionSuffix(String suffix) {
        this.actionSuffix = suffix;
    }
    
    @Inject(StrutsScopeConstants.CONVERSATION_IDLE_TIMEOUT)
    public void setDefaultMaxIdleTime(String defaultMaxIdleTimeString) {
        this.maxIdleTime = Long.parseLong(defaultMaxIdleTimeString);
    }

    @Inject(StrutsScopeConstants.CONVERSATION_ARBITRATOR_KEY)
    public void setArbitrator(ConversationArbitrator arbitrator) {
        this.arbitrator = arbitrator;
    }
    
    @Inject(StrutsScopeConstants.CONVERSATION_CONFIG_PROVIDER_KEY)
    public void setConversationConfigurationProvider(ConversationConfigurationProvider conversationConfigurationProvider) {
        this.conversationConfigurationProvider = conversationConfigurationProvider;
    }

    @Inject(StrutsScopeConstants.CONVERSATION_PROCESSOR_KEY)
    public void setConversationManager(ConversationProcessor manager) {
        this.conversationProcessor = manager;
    }

    @Inject(StrutsScopeConstants.CONVERSATION_CONTEXT_MANAGER_PROVIDER)
    public void setHttpConversationContextManagerProvider(HttpConversationContextManagerProvider conversationContextManagerProvider) {
        this.conversationContextManagerProvider = conversationContextManagerProvider;
    }
    
    @Inject(StrutsScopeConstants.CONVERSATION_MONITORING_THREAD_POOL_SIZE)
    public void setMonitoringThreadPoolSize(String monitoringThreadPoolSizeString) {
    	this.monitoringThreadPoolSize = Integer.parseInt(monitoringThreadPoolSizeString);
    }

    @Inject(StrutsScopeConstants.CONVERSATION_MONITORING_FREQUENCY)
    public void setMonitoringFrequency(String monitoringFrequencyString) {
    	this.monitoringFrequency = Long.parseLong(monitoringFrequencyString);
    }

    @Inject(StrutsScopeConstants.CONVERSATION_MAX_INSTANCES)
    public void setMaxInstances(String maxInstancesString) {
    	this.maxInstances = Integer.parseInt(maxInstancesString);
    }

    @Inject(StrutsScopeConstants.CONVERSATION_CONTEXT_FACTORY)
    public void setConversationContextFactory(ConversationContextFactory conversationContextFactory) {
        this.conversationContextFactory = conversationContextFactory;
    }
    
    @Inject(StrutsScopeConstants.EXPRESSION_EVAL)
    public void setEvalProviderName(String evalProviderName) {
        this.evalProviderName = evalProviderName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void destroy() {
        LOG.info("Destroying the ConversationInterceptor...");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init() {

        LOG.info("Initializing the Conversation Interceptor...");

        this.arbitrator.setActionSuffix(this.actionSuffix);

        this.conversationConfigurationProvider.setArbitrator(this.arbitrator);
        this.conversationConfigurationProvider.setDefaultMaxIdleTime(this.maxIdleTime);
        this.conversationConfigurationProvider.setDefaultMaxInstances(this.maxInstances);
        this.conversationConfigurationProvider.init(this.finder.getActionClasses());

        this.eval = this.container.getInstance(Eval.class, this.evalProviderName);
        if (this.eval == null) {
        	LOG.error("No bean with name " + this.evalProviderName + " of " + Eval.class + 
        			" was found.  Make sure the constant [" + StrutsScopeConstants.EXPRESSION_EVAL + "] is set to a valid value in your struts.xml." +
        					"  Defaulting to use OGNL as the expression evaluation provider.");
        } else {
        	LOG.info("Conversation expression evaluation provider [" + this.evalProviderName + "] successfully acquired.");
        }
        
        this.conversationProcessor.setConfigurationProvider(this.conversationConfigurationProvider);
        this.conversationProcessor.setEval(this.eval);
        
        this.conversationContextManagerProvider.setConversationContextFactory(this.conversationContextFactory);
        this.conversationContextManagerProvider.setMonitoringFrequency(this.monitoringFrequency);
        this.conversationContextManagerProvider.setMonitoringThreadPoolSize(this.monitoringThreadPoolSize);
        this.conversationContextManagerProvider.init();
        
        LOG.info("...Conversation Interceptor successfully initialized.");

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String doIntercept(ActionInvocation invocation) throws Exception {
    	
    	try {
    		
    		HttpServletRequest request = (HttpServletRequest) invocation.getInvocationContext().get(StrutsStatics.HTTP_REQUEST);
        	ConversationContextManager contextManager = this.conversationContextManagerProvider.getManager(request);
        	final ConversationAdapter adapter = new StrutsConversationAdapter(invocation, contextManager);
    		
    		this.conversationProcessor.processConversations(adapter);
    		
    		invocation.addPreResultListener(new PreResultListener() {

    			@Override
    			public void beforeResult(ActionInvocation invocation, String resultCode) {
    				adapter.executePostActionProcessors();
    				invocation.getStack().getContext().put(StrutsScopeConstants.CONVERSATION_ID_MAP_STACK_KEY, adapter.getViewContext());
    			}
            	
            });
    		
    		adapter.executePreActionProcessors();
            
            String result = invocation.invoke();
            
            adapter.executePostViewProcessors();
            
            if (Action.INPUT.equals(result)) {
            	result = this.handleInputResult(invocation, adapter);
            } else if (Action.ERROR.equals(result)) {
            	result = this.handleErrorResult(invocation, adapter);
            } else if (Action.LOGIN.equals(result)) {
            	result = this.handleLoginResult(invocation, adapter);
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
	 * <p>
	 * <tt>&ltparam name="conversationError">${conversationError}&lt/param></tt>
	 */
    protected void handleConversationErrorAware(ActionProxy proxy, String errorMessage) {
    	
		Object action = proxy.getAction();
		
		if (action instanceof ConversationErrorAware) {
			
			LOG.debug("Action is an instance of ConversationErrorAware; setting conversation error.");
			
			((ConversationErrorAware) action).setConversationError(errorMessage);
			
		}
    }
    
    protected String handleInputResult(ActionInvocation invocation, ConversationAdapter adapter) {
    	return Action.INPUT;
    }
    
    protected String handleErrorResult(ActionInvocation invocation, ConversationAdapter adapter) {
    	return Action.ERROR;
    }
    
    protected String handleLoginResult(ActionInvocation invocation, ConversationAdapter adapter) {
    	return Action.LOGIN;
    }
    
    protected String handleResult(String result, ActionInvocation invocation, ConversationAdapter adapter) {
    	adapter.executePostViewProcessors();
    	return result;
    }

}
