package com.google.code.rees.scope.conversation;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.code.rees.scope.conversation.annotations.BeginConversation;
import com.google.code.rees.scope.conversation.annotations.ConversationAction;
import com.google.code.rees.scope.conversation.annotations.ConversationController;
import com.google.code.rees.scope.conversation.annotations.ConversationField;
import com.google.code.rees.scope.conversation.annotations.EndConversation;
import com.google.code.rees.scope.util.NamingUtil;
import com.google.code.rees.scope.util.ReflectionUtil;

public class DefaultConversationArbitrator implements ConversationArbitrator {

	private static final long serialVersionUID = -1577464106543589370L;
	protected String actionSuffix = "Controller";
	
	public void setActionSuffix(String suffix) {
		this.actionSuffix  = suffix;
	}

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

	@Override
	public Collection<Method> getCandidateConversationMethods(Class<?> clazz) {
		Set<Method> conversationMethods = new HashSet<Method>();
		for (Method method : ReflectionUtil.getMethods(clazz)) {
			if (method.isAnnotationPresent(ConversationAction.class)) {
				conversationMethods.add(method);
			} else if (this.isConversationController(clazz) && this.isAction(method)) {
				conversationMethods.add(method);
			}
		}
		return conversationMethods;
	}

	@Override
	public Collection<String> getConversations(Class<?> clazz, Field field) {
		Set<String> fieldConversations = new HashSet<String>();
		if (field.isAnnotationPresent(ConversationField.class)) {
			ConversationField conversationField = field.getAnnotation(ConversationField.class);
			String[] conversations = conversationField.conversations();
			if (conversations.length == 0) {
				conversations = getConversationControllerConversations(clazz);
			}
			fieldConversations.addAll(Arrays.asList(conversations));
		}
		return fieldConversations;
	}

	@Override
	public Collection<String> getConversations(Class<?> clazz, Method method) {
		Set<String> methodConversations = new HashSet<String>();
		if (method.isAnnotationPresent(ConversationAction.class)) {
			ConversationAction conversationmethod = method.getAnnotation(ConversationAction.class);
			String[] conversations = conversationmethod.conversations();
			if (conversations.length == 0) {
				conversations = getConversationControllerConversations(clazz);
			}
			methodConversations.addAll(Arrays.asList(conversations));
		} else {
			methodConversations.addAll(Arrays.asList(this.getConversationControllerConversations(clazz)));
		}
		return methodConversations;
	}

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

	@Override
	public String getName(Method method) {
		return method.getName();
	}

	@Override
	public Collection<String> getBeginConversations(Class<?> clazz, Method method) {
		Set<String> methodConversations = new HashSet<String>();
		if (method.isAnnotationPresent(BeginConversation.class)) {
			BeginConversation conversationmethod = method.getAnnotation(BeginConversation.class);
			String[] conversations = conversationmethod.conversations();
			if (conversations.length == 0) {
				conversations = this.getConversationControllerConversations(clazz);
			}
			methodConversations.addAll(Arrays.asList(conversations));
		} else if (method.getName().startsWith("begin")) {
			methodConversations.addAll(Arrays.asList(this.getConversationControllerConversations(clazz)));
		}
		return methodConversations;
	}

	@Override
	public Collection<String> getEndConversations(Class<?> clazz, Method method) {
		Set<String> methodConversations = new HashSet<String>();
		if (method.isAnnotationPresent(EndConversation.class)) {
			EndConversation conversationmethod = method.getAnnotation(EndConversation.class);
			String[] conversations = conversationmethod.conversations();
			if (conversations.length == 0) {
				conversations = this.getConversationControllerConversations(clazz);
			}
			methodConversations.addAll(Arrays.asList(conversations));
		} else if (method.getName().startsWith("end")) {
			methodConversations.addAll(Arrays.asList(this.getConversationControllerConversations(clazz)));
		}
		return methodConversations;
	}
	
	protected boolean isAction(Method method) {
		String methodName = method.getName();
		return (!(methodName.startsWith("get") || methodName.startsWith("set") || methodName.startsWith("is"))
				&& Modifier.isPublic(method.getModifiers())
				&& !Modifier.isStatic(method.getModifiers())
				&& method.getReturnType().equals(String.class)
				&& method.getParameterTypes().length == 0);
	}
	
	protected boolean isConversationController(Class<?> clazz) {
		return this.getConversationControllers(clazz).size() > 0;
	}
	
	protected String[] getConversationControllerConversations(Class<?> clazz) {
		List<String> conversations = new ArrayList<String>();
		for (Class<?> conversationControllerClass : getConversationControllers(clazz)) {
			ConversationController controller = conversationControllerClass.getAnnotation(ConversationController.class);
			String[] newConversations = controller.conversations();
			if (controller.value().equals(ConversationController.DEFAULT_VALUE)) {
				if (newConversations.length == 0) {
					newConversations = new String[] { NamingUtil.getConventionName(conversationControllerClass, actionSuffix) };
				}
			} else {
				conversations.add(controller.value());
			}
			conversations.addAll(Arrays.asList(newConversations));
		}
		return conversations.toArray(new String[]{});
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

}
