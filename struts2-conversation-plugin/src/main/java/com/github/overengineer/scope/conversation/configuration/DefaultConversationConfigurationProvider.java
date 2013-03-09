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
 *  $Id: DefaultConversationConfigurationProvider.java reesbyars $
 ******************************************************************************/
package com.github.overengineer.scope.conversation.configuration;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.overengineer.scope.conversation.ConversationConstants;
import com.github.overengineer.scope.conversation.annotations.BeginConversation;
import com.github.overengineer.scope.conversation.annotations.EndConversation;
import com.github.overengineer.scope.conversation.annotations.Eval;
import com.github.overengineer.scope.util.ReflectionUtil;

/**
 * The default implementation of {@link ConversationConfigurationProvider}
 * 
 * TODO add default transacitonal and accessibleFromView settings, add new config settings to the log messages, add to config browser
 * 
 * @author rees.byars
 */
public class DefaultConversationConfigurationProvider implements ConversationConfigurationProvider {

    private static final long serialVersionUID = -1227350994518195549L;
    private static final Logger LOG = LoggerFactory.getLogger(DefaultConversationConfigurationProvider.class);

    protected ConversationArbitrator arbitrator;
    protected ConcurrentMap<Class<?>, Collection<ConversationClassConfiguration>> classConfigurations = new ConcurrentHashMap<Class<?>, Collection<ConversationClassConfiguration>>();
    protected ConcurrentMap<Class<?>, ExpressionConfiguration> expressionConfigurations = new ConcurrentHashMap<Class<?>, ExpressionConfiguration>();
	protected long maxIdleTimeMillis = ConversationConstants.DEFAULT_CONVERSATION_MAX_IDLE_TIME;
	protected int maxInstances = ConversationConstants.DEFAULT_MAXIMUM_NUMBER_OF_A_GIVEN_CONVERSATION;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setDefaultMaxIdleTime(long maxIdleTimeMillis) {
		double idleTimeHours = maxIdleTimeMillis / (1000.0 * 60 * 60);
    	LOG.info("Setting default conversation timeout:  " + maxIdleTimeMillis + " milliseconds.");
    	LOG.info("Converted default conversation timeout:  " + String.format("%.2f", idleTimeHours) + " hours.");
		this.maxIdleTimeMillis = maxIdleTimeMillis;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setDefaultMaxInstances(int maxInstances) {
		LOG.info("Setting max number of conversation instances per conversation:  " + maxInstances + ".");
		this.maxInstances = maxInstances;
	}

    /**
     * {@inheritDoc}
     */
    @Override
    public void setArbitrator(ConversationArbitrator arbitrator) {
        this.arbitrator = arbitrator;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init(Set<Class<?>> actionClasses) {
    	if (this.classConfigurations.size() != actionClasses.size()) { //in case it's already been called
    		LOG.info("Building Conversation Configurations...");
        	if (this.arbitrator == null) {
        		LOG.error("No ConversationArbitrator set for the ConversationConfigurationProvider, review configuration files to make sure an arbitrator is declared.");
        	}
            for (Class<?> clazz : actionClasses) {
                processClass(clazz, classConfigurations);
            }
            LOG.info("...building of Conversation Configurations successfully completed.");
    	}
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<ConversationClassConfiguration> getConfigurations(Class<?> clazz) {
        Collection<ConversationClassConfiguration> configurations = classConfigurations.get(clazz);
        if (configurations == null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("No cached ConversationClassConfiguration found for class " + clazz.getName());
            }
            configurations = this.processClass(clazz, classConfigurations);
        }
        return configurations;
    }

    /**
     * good candidate for refactoring... but it works!
     * 
     * @param clazz
     * @param classConfigurations
     * @return
     */
    protected Collection<ConversationClassConfiguration> processClass(Class<?> clazz, ConcurrentMap<Class<?>, Collection<ConversationClassConfiguration>> classConfigurations) {
        Collection<ConversationClassConfiguration> configurations = classConfigurations.get(clazz);
        if (configurations == null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Building ConversationClassConfiguration for class " + clazz.getName());
            }
            configurations = new HashSet<ConversationClassConfiguration>();
            Map<String, ConversationClassConfiguration> temporaryConversationMap = new HashMap<String, ConversationClassConfiguration>();
            ExpressionConfiguration expressionConfiguration = new ExpressionConfigurationImpl();
            
            for (Field field : this.arbitrator.getCandidateConversationFields(clazz)) {
                Collection<String> fieldConversations = this.arbitrator.getConversations(clazz, field);
                if (fieldConversations != null) {
                    String fieldName = this.arbitrator.getName(field);
                    ReflectionUtil.makeAccessible(field);
                    for (String conversation : fieldConversations) {
                        ConversationClassConfiguration configuration = temporaryConversationMap.get(conversation);
                        if (configuration == null) {
                            configuration = new ConversationClassConfigurationImpl(conversation);
                            temporaryConversationMap.put(conversation, configuration);
                        }
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Adding field " + fieldName + " to ConversationClassConfiguration for Conversation " + conversation);
                        }
                        configuration.addField(fieldName, field);
                    }
                }
            }
            
            // TODO refactor into multiple methods to make more beautimous instead of atrocious
            for (Method method : this.arbitrator.getCandidateConversationMethods(clazz)) {
            	
            	String methodName = this.arbitrator.getName(method);
            	
            	this.resolveExpressionConfig(clazz, method, methodName, expressionConfiguration);
            	expressionConfigurations.put(clazz, expressionConfiguration);
            	
            	//intermediate action methods
                Collection<String> methodConversations = this.arbitrator.getConversations(clazz, method);
                if (methodConversations != null) {
                    for (String conversation : methodConversations) {
                        ConversationClassConfiguration configuration = temporaryConversationMap.get(conversation);
                        if (configuration == null) {
                            configuration = new ConversationClassConfigurationImpl(conversation);
                            temporaryConversationMap.put(conversation, configuration);
                        }
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Adding method " + methodName + " as an Action to ConversationClassConfiguration for Conversation " + conversation);
                        }
                        configuration.addAction(methodName);
                    }
                }
                
                //begin action methods
                Collection<String> methodBeginConversations = this.arbitrator.getBeginConversations(clazz, method);
                if (methodBeginConversations != null) {
                    for (String conversation : methodBeginConversations) {
                        ConversationClassConfiguration configuration = temporaryConversationMap.get(conversation);
                        if (configuration == null) {
                            configuration = new ConversationClassConfigurationImpl(conversation);
                            temporaryConversationMap.put(conversation, configuration);
                        }
                		
                		if (LOG.isDebugEnabled()) {
                            LOG.debug("Adding method " + methodName + " as a Begin Action to ConversationClassConfiguration for Conversation");
                        }
                        if (method.isAnnotationPresent(BeginConversation.class)) {
                        	BeginConversation config = method.getAnnotation(BeginConversation.class);
                        	long maxIdle = config.maxIdleTimeMillis();
                        	if (maxIdle == -1L) {
                        		maxIdle = this.maxIdleTimeMillis;
                        	}
                        	int maxInstance = config.maxInstances();
                        	if (maxInstance == 0) {
                        		maxInstance = this.maxInstances;
                        	}
                        	configuration.addBeginAction(methodName, maxIdle, config.maxIdleTime(), maxInstance, config.transactional());
                        } else {
                        	configuration.addBeginAction(methodName, this.maxIdleTimeMillis, "", this.maxInstances, false);
                        }
                		
                    }
                }
                
                //end action methods
                Collection<String> methodEndConversations = this.arbitrator.getEndConversations(clazz, method);
                if (methodEndConversations != null) {
                    for (String conversation : methodEndConversations) {
                        ConversationClassConfiguration configuration = temporaryConversationMap.get(conversation);
                        if (configuration == null) {
                            configuration = new ConversationClassConfigurationImpl(conversation);
                            temporaryConversationMap.put(conversation, configuration);
                        }
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Adding method " + methodName + " as an End Action to ConversationClassConfiguration for Conversation " + conversation);
                        }
                        if (method.isAnnotationPresent(EndConversation.class)) {
                        	EndConversation config = method.getAnnotation(EndConversation.class);
                        	configuration.addEndAction(methodName, config.accessibleFromView());
                        } else {
                        	configuration.addEndAction(methodName, false);
                        }
                    }
                }
            }
            
            configurations.addAll(temporaryConversationMap.values());
            classConfigurations.putIfAbsent(clazz, configurations);
        }
        
        return configurations;
    }

    /**
     * {@inheritDoc}
     */
	@Override
	public ExpressionConfiguration getExpressionConfiguration(Class<?> actionClass) {
		return this.expressionConfigurations.get(actionClass);
	}
	
	protected void resolveExpressionConfig(Class<?> clazz, Method method, String methodName, ExpressionConfiguration expressionConfiguration) {
		if (method.isAnnotationPresent(Eval.class)) {
			Eval eval = method.getAnnotation(Eval.class);
			expressionConfiguration.addExpressions(methodName, eval.preAction(), eval.postAction(), eval.postView());
		} else if (clazz.isAnnotationPresent(Eval.class)) {
			Eval eval = clazz.getAnnotation(Eval.class);
			expressionConfiguration.addExpressions(methodName, eval.preAction(), eval.postAction(), eval.postView());
		} else {
			expressionConfiguration.addExpressions(methodName, "", "", "");
		}
	}

}
