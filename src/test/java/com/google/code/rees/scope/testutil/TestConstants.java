package com.google.code.rees.scope.testutil;

import java.util.HashSet;
import java.util.Set;

import com.google.code.rees.scope.mocks.actions.AbstractController;
import com.google.code.rees.scope.mocks.actions.InterfaceController;
import com.google.code.rees.scope.mocks.actions.MockActionByConfiguration;
import com.google.code.rees.scope.mocks.actions.MockActionInterfaceImpl;
import com.google.code.rees.scope.mocks.actions.MockConventionController;
import com.google.code.rees.scope.mocks.actions.MockNoSessionFieldController;
import com.google.code.rees.scope.mocks.actions.MockPojoController;
import com.google.code.rees.scope.mocks.actions.conversation.MockConversationController;
import com.google.code.rees.scope.mocks.actions.conversation.MockConversationControllerInterface;

public class TestConstants {
	
	public static final Set<Class<?>> ALL_ACTION_CLASSES = initAllActionClasses();
	public static final Set<Class<?>> ALL_CONVENTION_ACTION_CLASSES = initConventionActionClasses();
	public static final Set<Class<?>> SESSION_FIELD_ACTION_CLASSES = initSessionFieldActionClasses();
	public static final Set<Class<?>> NO_SESSION_FIELD_ACTION_CLASSES = initNoSessionFieldActionClasses();
	
	private static Set<Class<?>> initAllActionClasses() {
		Set<Class<?>> actionClasses = new HashSet<Class<?>>();
		actionClasses.add(MockConversationControllerInterface.class);
		actionClasses.add(MockActionByConfiguration.class);
		actionClasses.addAll(initConventionActionClasses());
		return actionClasses;
	}
	
	private static Set<Class<?>> initSessionFieldActionClasses() {
		Set<Class<?>> actionClasses = new HashSet<Class<?>>();
		actionClasses.add(MockActionByConfiguration.class);
		actionClasses.add(MockActionInterfaceImpl.class);
		actionClasses.add(MockConventionController.class);
		actionClasses.add(MockPojoController.class);
		return actionClasses;
	}
	
	private static Set<Class<?>> initNoSessionFieldActionClasses() {
		Set<Class<?>> actionClasses = new HashSet<Class<?>>();
		actionClasses.add(MockNoSessionFieldController.class);
		return actionClasses;
	}
	
	private static Set<Class<?>> initConventionActionClasses() {
		Set<Class<?>> actionClasses = new HashSet<Class<?>>();
		actionClasses.add(InterfaceController.class);
		actionClasses.add(AbstractController.class);
		actionClasses.add(MockActionInterfaceImpl.class);
		actionClasses.add(MockConventionController.class);
		actionClasses.add(MockPojoController.class);
		actionClasses.add(MockNoSessionFieldController.class);
		actionClasses.add(MockConversationController.class);
		return actionClasses;
	}

}
