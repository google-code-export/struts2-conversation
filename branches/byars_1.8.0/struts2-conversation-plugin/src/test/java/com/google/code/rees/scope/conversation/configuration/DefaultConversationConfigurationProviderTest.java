package com.google.code.rees.scope.conversation.configuration;

import org.easymock.EasyMock;
import org.junit.Test;

import com.google.code.rees.scope.mocks.actions.conversation.MockConversationController;
import com.google.code.rees.scope.testutil.SerializableObjectTest;
import com.google.code.rees.scope.util.thread.BasicTaskThread;
import com.google.code.rees.scope.util.thread.ThreadTask;
import com.opensymphony.xwork2.Action;

public class DefaultConversationConfigurationProviderTest extends
        SerializableObjectTest<DefaultConversationConfigurationProvider> {
	
	@Test
	public void testConcurrency() throws InterruptedException {
		
		ConversationConfigurationProvider provider = new DefaultConversationConfigurationProvider();
		provider.setArbitrator(new DefaultConversationArbitrator());
		
		for (int i = 0; i < 300; i++) {
			BasicTaskThread.spawnInstance().addTask(new ConfigurationRequestor1(provider));
			BasicTaskThread.spawnInstance().addTask(new ConfigurationRequestor2(provider));
		}
		
		Thread.sleep(10000L);
		
	}
	
	class ConfigurationRequestor1 implements ThreadTask {
		ConversationConfigurationProvider provider;
		ConfigurationRequestor1(ConversationConfigurationProvider provider) {
			this.provider = provider;
		}

		@Override
		public boolean isActive() {
			return true;
		}

		@Override
		public void cancel() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void doTask() {
			this.provider.getConfigurations(EasyMock.createMock(Action.class).getClass());
		}
		
	}
	
	class ConfigurationRequestor2 implements ThreadTask {
		ConversationConfigurationProvider provider;
		ConfigurationRequestor2(ConversationConfigurationProvider provider) {
			this.provider = provider;
		}

		@Override
		public boolean isActive() {
			return true;
		}

		@Override
		public void cancel() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void doTask() {
			this.provider.getConfigurations(MockConversationController.class);
		}
		
	}

}
