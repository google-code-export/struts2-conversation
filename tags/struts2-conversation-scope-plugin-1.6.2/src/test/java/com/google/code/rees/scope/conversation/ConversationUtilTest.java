package com.google.code.rees.scope.conversation;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;

import com.google.code.rees.scope.conversation.context.ConversationContext;
import com.google.code.rees.scope.conversation.context.DefaultConversationContextFactory;
import com.google.code.rees.scope.conversation.context.DefaultHttpConversationContextManagerFactory;
import com.google.code.rees.scope.conversation.context.HttpConversationContextManagerFactory;
import com.google.code.rees.scope.mocks.MockConversationAdapter;
import com.google.code.rees.scope.util.monitor.TimeoutListener;

public class ConversationUtilTest implements TimeoutListener<ConversationContext> {
	
	private static final long serialVersionUID = 1L;
	
	private boolean timedout = false;
	private String mockConversationName = "test_conversation";
	private String mockConversationId = "123";
	
	@Test
	public void testSetTimeout() {
		MockHttpSession session = new MockHttpSession();
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setSession(session);
		request.setParameter(mockConversationName, mockConversationId);
		HttpConversationContextManagerFactory managerFactory = new DefaultHttpConversationContextManagerFactory();
		managerFactory.setConversationContextFactory(new DefaultConversationContextFactory());
		managerFactory.setMonitoringFrequency(1L);
		managerFactory.init();
		MockConversationAdapter.init(request, managerFactory);
		ConversationContext context = ConversationUtil.begin(mockConversationName, 50L);
		context.addTimeoutListener(this);
		context.setMaxIdleTime(50L);
		try {
			Thread.sleep(2000L);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		assertTrue(this.timedout);
	}

	@Override
	public void onTimeout(ConversationContext timeoutable) {
		this.timedout = true;
	}

}
