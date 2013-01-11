package com.google.code.rees.scope.testutil;

import java.util.Map;

import com.google.code.rees.scope.conversation.ConversationAdapter;
import com.google.code.rees.scope.conversation.context.ConversationContext;
import com.google.code.rees.scope.struts2.test.ScopeTestUtil;
import com.google.code.struts2.test.junit.StrutsSpringTest;
import com.opensymphony.xwork2.ActionInvocation;

/**
 * 
 * @author rees.byars
 * 
 */
public abstract class StrutsSpringScopeTestCase<T> extends StrutsSpringTest<T> {
    
    @Override
	public void beforeResult(ActionInvocation invocation, String resultCode) {
    	final ConversationAdapter adapter = ConversationAdapter.getAdapter();;
    	ConversationAdapter.setAdapter(new ConversationAdapter() {

			private static final long serialVersionUID = 1L;

			@Override
			public Object getAction() {
				return adapter.getAction();
			}

			@Override
			public String getActionId() {
				return adapter.getActionId();
			}

			@Override
			public Map<String, Object> getActionContext() {
				return adapter.getActionContext();
			}

			@Override
			public Map<String, String> getRequestContext() {
				return adapter.getRequestContext();
			}

			@Override
			public ConversationContext beginConversation(
					String conversationName, long maxIdleTimeMillis, int maxInstances) {
				return adapter.beginConversation(conversationName, maxIdleTimeMillis, maxInstances);
			}

			@Override
			public ConversationContext getConversationContext(
					String conversationName, String conversationId) {
				return adapter.getConversationContext(conversationName, conversationId);
			}

			@Override
			public ConversationContext endConversation(String conversationName,
					String conversationId) {
				return adapter.endConversation(conversationName, conversationId);
			}
			
			@Override
			public void doCleanup() {
				conversationAdapter.set(adapter);
			}
    		
    	});
    }
    
    @Override
    public void afterProxyExecution(ActionInvocation invocation, String result) {
        ScopeTestUtil.injectScopeFields(this);
        super.afterProxyExecution(invocation, result);
    }
}
