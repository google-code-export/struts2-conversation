package com.google.code.rees.scope.expression;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.junit.After;
import org.junit.Before;
import org.springframework.mock.web.MockHttpServletRequest;

import com.google.code.rees.scope.conversation.ConversationAdapter;
import com.google.code.rees.scope.conversation.context.ConversationContextManager;
import com.google.code.rees.scope.conversation.context.DefaultConversationContextFactory;
import com.google.code.rees.scope.conversation.context.DefaultConversationContextManager;
import com.google.code.rees.scope.conversation.context.DefaultHttpConversationContextManagerProvider;
import com.google.code.rees.scope.conversation.context.HttpConversationContextManagerProvider;
import com.google.code.rees.scope.mocks.MockConversationAdapter;

//TODO:  add mock adapter and test the conversation functions
//TODO:  add active conversation context exploration with three columns:  name/key, class, toString
public abstract class EvalTest {
	
	protected String mockConversationName = "mock_conversation";
	protected String mockConversationId = "1";
	protected MockHttpServletRequest request = new MockHttpServletRequest();
	protected ConversationContextManager contextManager = new DefaultConversationContextManager();
	protected HttpConversationContextManagerProvider contextManagerProvider = new DefaultHttpConversationContextManagerProvider() {
		
		private static final long serialVersionUID = 1L;

		@Override
		public ConversationContextManager getManager(HttpServletRequest request) {
			return contextManager;
		}
	};
	protected Eval eval;
	private TestBean bean1 = new TestBean();
	private TestBean bean2 = new TestBean();
	Map<String, Object> context = new HashMap<String, Object>();
	ConversationAdapter adapter = MockConversationAdapter.init(request, contextManagerProvider);
	
	@Before
	public void setUp() {
		bean1.setName("supa");
		bean1.setValue(42);
		
		bean2.setName("dupa");
		bean2.setValue(69);
		
		context.put(bean1.getName(), bean1);
		context.put(bean2.getName(), bean2);
		
		request.setParameter(mockConversationName, mockConversationId);
		
		this.contextManager.setContextFactory(new DefaultConversationContextFactory());
		this.contextManager.createContext(mockConversationName, 1L);
	}
	
	@After
	public void tearDown() {
		this.contextManager.remove(mockConversationName, mockConversationId);
		this.contextManager.destroy();
	}
	
	public TestBean getBean1() {
		return this.bean1;
	}
	
	public TestBean getBean2() {
		return this.bean2;
	}

}
