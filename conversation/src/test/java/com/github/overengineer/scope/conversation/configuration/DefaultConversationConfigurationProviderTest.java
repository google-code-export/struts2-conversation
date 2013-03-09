package com.github.overengineer.scope.conversation.configuration;

import org.easymock.EasyMock;
import org.junit.Test;

import com.github.overengineer.scope.conversation.configuration.ConversationConfigurationProvider;
import com.github.overengineer.scope.conversation.configuration.DefaultConversationArbitrator;
import com.github.overengineer.scope.conversation.configuration.DefaultConversationConfigurationProvider;
import com.github.overengineer.scope.mocks.actions.conversation.MockConversationController;
import com.github.overengineer.scope.testutil.SerializableObjectTest;
import com.github.overengineer.scope.testutil.thread.BasicTaskThread;
import com.github.overengineer.scope.testutil.thread.ThreadTask;
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
