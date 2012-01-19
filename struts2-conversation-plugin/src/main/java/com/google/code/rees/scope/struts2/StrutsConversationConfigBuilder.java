package com.google.code.rees.scope.struts2;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.util.ClassLoaderUtils;

import com.google.code.rees.scope.conversation.BeginConversation;
import com.google.code.rees.scope.conversation.ConversationAction;
import com.google.code.rees.scope.conversation.ConversationConfiguration;
import com.google.code.rees.scope.conversation.ConversationConfigBuilder;
import com.google.code.rees.scope.conversation.ConversationController;
import com.google.code.rees.scope.conversation.ConversationField;
import com.google.code.rees.scope.conversation.EndConversation;
import com.google.code.rees.scope.request.RequestField;
import com.google.code.rees.scope.session.SessionField;
import com.google.code.rees.scope.util.NamingUtil;
import com.google.code.rees.scope.util.ReflectionUtil;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;

/**
 * 
 * @author rees.byars
 * 
 */
public class StrutsConversationConfigBuilder implements ConversationConfigBuilder {

	private static final Logger LOG = LoggerFactory
			.getLogger(StrutsConversationConfigBuilder.class);
	private static Map<Class<?>, String[]> classConversationNamesMap;
	private ActionFinder finder;
	private Map<Class<?>, Collection<ConversationConfiguration>> conversationConfigs;
	private Set<Class<?>> classesProcessed;
	private Set<Class<? extends Annotation>> overridingAnnotations;
	private boolean followsConvention;
	private boolean actionAnnotationExists;
	private String actionSuffix;
	
	public StrutsConversationConfigBuilder() {
		this.followsConvention = true;
		this.initOverridingAnnotations();
		this.actionAnnotationExists = ReflectionUtil.classExists("org.apache.struts2.convention.annotation.Action");
	}
	
	@Inject(StrutsScopeConstants.OVERRIDING_ANNOTATIONS)
	public void setOverridingAnnotations(String commaSeparatedAnnotationNames) {
		for (String annotationName : commaSeparatedAnnotationNames.split(",")) {
			addAnnotation(annotationName.trim(), this.overridingAnnotations);
		}
	}

	@Inject(StrutsScopeConstants.ACTION_FINDER_KEY)
	public void setActionClassFinder(ActionFinder finder) {
		this.finder = finder;
	}
	
	@Inject(ConventionConstants.ACTION_SUFFIX)
	public void setActionSuffix(String suffix) {
		this.actionSuffix = suffix;
	}
	
	@Inject(value = StrutsScopeConstants.REQUIRE_FOLLOWS_CONVENTION)
	public void setRequireFollowsConvention(String requireFollowsConvention) {
		this.followsConvention = "true".equals(requireFollowsConvention);
	}
	
	@Override
	public synchronized Map<Class<?>, Collection<ConversationConfiguration>> getConversationConfigs() {
		if (conversationConfigs == null) {
			buildConversationConfigMap();
		}
		return Collections.unmodifiableMap(conversationConfigs);
	}
	
	protected void buildConversationConfigMap() {
		conversationConfigs = new HashMap<Class<?>, Collection<ConversationConfiguration>>();
		this.classesProcessed = new HashSet<Class<?>>();
		classConversationNamesMap = new HashMap<Class<?>, String[]>();
		Set<Class<?>> actionClasses = finder.getActionClasses();
		for (Class<?> clazz : actionClasses) {
			addClassConfig(clazz);
		}
	}
	
	@Override
	public synchronized Collection<ConversationConfiguration> addClassConfig(Class<?> clazz) {
		if (!this.classesProcessed.contains(clazz) && !clazz.isInterface() && !Modifier.isAbstract(clazz.getModifiers()) && !clazz.isAnnotation()) {
			Map<String, ConversationConfiguration> classConversationConfigs = new HashMap<String, ConversationConfiguration>();
			addConversationFields(clazz, classConversationConfigs);
			addConversationControllerFields(clazz, classConversationConfigs, this.getOverridingAnnotations());
			addConversationActionMethods(clazz, classConversationConfigs);
			addConversationControllerMethods(clazz, classConversationConfigs, followsConvention, actionAnnotationExists);
			classesProcessed.add(clazz);
			List<String> cNamesList = new ArrayList<String>();
			cNamesList.addAll(classConversationConfigs.keySet());
			classConversationNamesMap.put(clazz, cNamesList.toArray(new String[cNamesList.size()]));
			conversationConfigs.put(clazz, Collections.unmodifiableCollection(classConversationConfigs.values()));
		}
		return conversationConfigs.get(clazz);
	}
	
	public static String[] getConversationNames(Class<?> actionClass) {
		return classConversationNamesMap.get(actionClass);
	}
	
	protected void initOverridingAnnotations() {
		overridingAnnotations = new HashSet<Class<? extends Annotation>>();
		overridingAnnotations.add(RequestField.class);
		overridingAnnotations.add(SessionField.class);
		overridingAnnotations.add(Inject.class);
		overridingAnnotations.add(Resource.class);
	}
	
	protected Set<Class<? extends Annotation>> getOverridingAnnotations() {
		return overridingAnnotations;
	}
	
	@SuppressWarnings("unchecked")
	protected void addAnnotation(String className, Set<Class<? extends Annotation>> annotationSet) {
		try {
			annotationSet.add(ClassLoaderUtils.loadClass(className, StrutsConversationConfigBuilder.class));
		} catch (ClassNotFoundException e) {
			LOG.warn("Annotation class not found:  " + className);
		} catch (ClassCastException cce) {
			LOG.warn("Class does not extend Annotation.class:  " + className);
		}
	}
	
	protected void addConversationFields(Class<?> clazz, Map<String, ConversationConfiguration> conversationConfigs) {
		
		for (Field field : ReflectionUtil.getFields(clazz)) {
			if (field.isAnnotationPresent(ConversationField.class)) {
				ConversationField conversationField = field
						.getAnnotation(ConversationField.class);
				String name = conversationField.name();
				if (name.equals(ConversationField.DEFAULT)) {
					name = field.getName();
				}
				String[] conversations = conversationField.conversations();
				if (conversations.length == 0) {
					conversations = getConversationControllerConversations(clazz);
				}
				ReflectionUtil.makeAccessible(field);
				for (String conversation : conversations) {
					conversation = this.sanitizeConversationName(conversation);
					ConversationConfiguration conversationConfig = conversationConfigs.get(conversation);
					if (conversationConfig == null) {
						conversationConfig = new ConversationConfiguration(conversation);
						conversationConfigs.put(conversation, conversationConfig);
					}
					LOG.debug("Adding field " + name + " to conversation " + conversation + " for class " + clazz.getSimpleName());
					conversationConfig.addField(name, field);
				}
			}
		}
	}
	
	protected void addConversationControllerFields(Class<?> clazz, Map<String, ConversationConfiguration> conversationConfigs,
			Set<Class<? extends Annotation>> overridingAnnotations) {
		
		String[] conversations = getConversationControllerConversations(clazz);
		if (conversations.length > 0) {
			for (Field field : ReflectionUtil.getFields(clazz)) {
				if (!Modifier.isStatic(field.getModifiers()) 
						&& !Modifier.isFinal(field.getModifiers())
						&& !field.isAnnotationPresent(ConversationField.class)) {
					boolean overridden = false;
					for (Class<? extends Annotation> annotation : overridingAnnotations) {
						if (field.isAnnotationPresent(annotation)) {
							overridden = true;
							break;
						}
					}
					if (!overridden) {
						ReflectionUtil.makeAccessible(field);
						for (String conversation : conversations) {
							conversation = this.sanitizeConversationName(conversation);
							ConversationConfiguration conversationConfig = conversationConfigs.get(conversation);
							if (conversationConfig == null) {
								conversationConfig = new ConversationConfiguration(conversation);
								conversationConfigs.put(conversation, conversationConfig);
							}
							LOG.debug("Adding field " + field.getName() + " to conversation " + conversation + " for class " + clazz.getSimpleName());
							conversationConfig.addField(field.getName(), field);
						}
					}
				}
			}
		}
	}

	protected void addConversationActionMethods(Class<?> clazz, Map<String, ConversationConfiguration> conversationConfigs) {
		for (Method method : ReflectionUtil.getMethods(clazz)) {
			if (method.isAnnotationPresent(ConversationAction.class)) {
				ConversationAction conversationAction = method
						.getAnnotation(ConversationAction.class);
				String[] conversations = conversationAction.conversations();
				if (conversations.length == 0) {
					conversations = getConversationControllerConversations(clazz);
				}
				addConversationMethod(method, conversations, conversationConfigs);
			}
			if (method.isAnnotationPresent(BeginConversation.class)) {
				BeginConversation conversationBegin = method
						.getAnnotation(BeginConversation.class);
				String[] conversations = conversationBegin.conversations();
				if (conversations.length == 0) {
					conversations = getConversationControllerConversations(clazz);
				}
				addConversationBeginMethod(method, conversations, conversationConfigs);
			}
			if (method.isAnnotationPresent(EndConversation.class)) {
				EndConversation conversationEnd = method
						.getAnnotation(EndConversation.class);
				String[] conversations = conversationEnd.conversations();
				if (conversations.length == 0) {
					conversations = getConversationControllerConversations(clazz);
				}
				addConversationEndMethod(method, conversations, conversationConfigs);
			}
		}
	}
	
	protected void addConversationControllerMethods(Class<?> clazz, Map<String, ConversationConfiguration> conversationConfigs,
			boolean followsConvention, boolean actionAnnotationExists) {

		String[] conversations = getConversationControllerConversations(clazz);
		if (conversations.length > 0) {
			for (Method method : ReflectionUtil.getMethods(clazz)) {
				String methodName = method.getName();
				if (followsConvention) {
					if (methodName.equals("execute")) {
						addConversationMethod(method, conversations, conversationConfigs);
					} else if (actionAnnotationExists) {
						if (method.isAnnotationPresent(Action.class)) {
							addConversationMethod(method, conversations, conversationConfigs);
							if (methodName.startsWith("begin")) {
								addConversationBeginMethod(method, conversations, conversationConfigs);
							} else if (methodName.startsWith("end")) {
								addConversationEndMethod(method, conversations, conversationConfigs);
							}
						}
					}	
				} else if (!(methodName.startsWith("get") || methodName.startsWith("set") || methodName.startsWith("is"))
							&& Modifier.isPublic(method.getModifiers())
							&& !Modifier.isStatic(method.getModifiers())
							&& method.getReturnType().equals(String.class)
							&& method.getParameterTypes().length == 0) {
					addConversationMethod(method, conversations, conversationConfigs);
				}
			}
		}
	}
	
	protected void addConversationMethod(Method method, String[] conversations, Map<String, ConversationConfiguration> conversationConfigs) {
		for (String conversation : conversations) {
			conversation = this.sanitizeConversationName(conversation);
			ConversationConfiguration conversationConfig = conversationConfigs
					.get(conversation);
			if (conversationConfig == null) {
				conversationConfig = new ConversationConfiguration(conversation);
				conversationConfigs.put(conversation, conversationConfig);
			}
			LOG.debug("Adding method " + method.getName() + " to conversation " + conversation);
			conversationConfig.addAction(method.getName());
		}
	}
	
	protected void addConversationBeginMethod(Method method, String[] conversations, Map<String, ConversationConfiguration> conversationConfigs) {
		for (String conversation : conversations) {
			conversation = this.sanitizeConversationName(conversation);
			ConversationConfiguration conversationConfig = conversationConfigs
					.get(conversation);
			if (conversationConfig == null) {
				conversationConfig = new ConversationConfiguration(conversation);
				conversationConfigs.put(conversation, conversationConfig);
			}
			LOG.debug("Adding Begin method " + method.getName() + " to conversation " + conversation);
			conversationConfig.addBeginAction(method.getName());
		}
	}
	
	protected void addConversationEndMethod(Method method, String[] conversations, Map<String, ConversationConfiguration> conversationConfigs) {
		for (String conversation : conversations) {
			conversation = this.sanitizeConversationName(conversation);
			ConversationConfiguration conversationConfig = conversationConfigs
					.get(conversation);
			if (conversationConfig == null) {
				conversationConfig = new ConversationConfiguration(conversation);
				conversationConfigs.put(conversation, conversationConfig);
			}
			LOG.debug("Adding End method " + method.getName() + " to conversation " + conversation);
			conversationConfig.addEndAction(method.getName());
		}
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
	
	protected String sanitizeConversationName(String conversationName) {
		return conversationName.replaceAll(":", "").replaceAll(",", "");
	}

}
