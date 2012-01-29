package com.google.code.rees.scope.conversation;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.code.rees.scope.util.ReflectionUtil;

/**
 * 
 * @author rees.byars
 * 
 */
public class DefaultConversationConfigurationProvider implements ConversationConfigurationProvider {

	private static final long serialVersionUID = -1227350994518195549L;
	
	protected ConversationArbitrator arbitrator;
	protected Map<Class<?>, Collection<ConversationConfiguration>> classConfigurations;

	@Override
	public void init() {
		classConfigurations = new HashMap<Class<?>, Collection<ConversationConfiguration>>();
	}
	
	@Override
	public void init(Set<Class<?>> actionClasses) {
		this.init();
		for (Class<?> clazz : actionClasses) {
			processClass(clazz, classConfigurations);
		}
	}

	@Override
	public void setArbitrator(ConversationArbitrator arbitrator) {
		this.arbitrator = arbitrator;
	}

	@Override
	public Collection<ConversationConfiguration> getConfigurations(Class<?> clazz) {
		Collection<ConversationConfiguration> configurations = classConfigurations.get(clazz);
		if (configurations == null) {
			configurations = this.processClass(clazz, classConfigurations);
		}
		return configurations;
	}
	
	protected synchronized Collection<ConversationConfiguration> processClass(Class<?> clazz, Map<Class<?>, Collection<ConversationConfiguration>> classConfigurations) {
		Collection<ConversationConfiguration> configurations = classConfigurations.get(clazz);
		if (configurations == null) {
			configurations = new HashSet<ConversationConfiguration>();
			Map<String, ConversationConfiguration> temporaryConversationMap = new HashMap<String, ConversationConfiguration>();
			for (Field field : this.arbitrator.getCandidateConversationFields(clazz)) {
				Collection<String> fieldConversations = this.arbitrator.getConversations(clazz, field);
				if (fieldConversations != null) {
					String fieldName = this.arbitrator.getName(field);
					ReflectionUtil.makeAccessible(field);
					for (String conversation : fieldConversations) {
						ConversationConfiguration configuration = temporaryConversationMap.get(conversation);
						if (configuration == null) {
							configuration = new ConversationConfiguration(conversation);
							temporaryConversationMap.put(conversation, configuration);
						}
						configuration.addField(fieldName, field);
					}
				}
			}
			//TODO refactor into multiple methods to make more beautimous
			for (Method method : this.arbitrator.getCandidateConversationMethods(clazz)) {
				Collection<String> methodConversations = this.arbitrator.getConversations(clazz, method);
				if (methodConversations != null) {
					String methodName = this.arbitrator.getName(method);
					for (String conversation : methodConversations) {
						ConversationConfiguration configuration = temporaryConversationMap.get(conversation);
						if (configuration == null) {
							configuration = new ConversationConfiguration(conversation);
							temporaryConversationMap.put(conversation, configuration);
						}
						configuration.addAction(methodName);
					}
				}
				Collection<String> methodBeginConversations = this.arbitrator.getBeginConversations(clazz, method);
				if (methodBeginConversations != null) {
					String methodName = this.arbitrator.getName(method);
					for (String conversation : methodBeginConversations) {
						ConversationConfiguration configuration = temporaryConversationMap.get(conversation);
						if (configuration == null) {
							configuration = new ConversationConfiguration(conversation);
							temporaryConversationMap.put(conversation, configuration);
						}
						configuration.addBeginAction(methodName);
					}
				}
				Collection<String> methodEndConversations = this.arbitrator.getEndConversations(clazz, method);
				if (methodEndConversations != null) {
					String methodName = this.arbitrator.getName(method);
					for (String conversation : methodEndConversations) {
						ConversationConfiguration configuration = temporaryConversationMap.get(conversation);
						if (configuration == null) {
							configuration = new ConversationConfiguration(conversation);
							temporaryConversationMap.put(conversation, configuration);
						}
						configuration.addEndAction(methodName);
					}
				}
			}
			configurations.addAll(temporaryConversationMap.values());
			classConfigurations.put(clazz, configurations);
		}
		return configurations;
	}

}
