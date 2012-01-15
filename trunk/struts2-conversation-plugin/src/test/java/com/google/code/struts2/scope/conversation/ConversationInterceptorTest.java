package com.google.code.struts2.scope.conversation;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import com.google.code.struts2.scope.mocks.actions.conversation.MockConversationController;
import com.google.code.struts2.scope.sessionfield.SessionField;
import com.google.code.struts2.scope.test.ScopeTestUtil;
import com.google.code.struts2.scope.testutil.ScopeTestCase;
import com.google.code.struts2.test.junit.StrutsConfiguration;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.inject.Inject;

@StrutsConfiguration(locations = "struts-convention.xml")
public class ConversationInterceptorTest extends
		ScopeTestCase<MockConversationController> {

	static final String CONVERSATION_NAME = "oopy-conversation";
	static final String CONVERSATION_FIELD = "conversationString";

	@Inject(ConversationConstants.MANAGER_KEY)
	ConversationManager manager;
	
	@ConversationField(conversations = "oopy")
	String conversationString;
	
	@SessionField
	String chubby;

	@Test
	public void testContinueRegistration() throws Exception {

		conversationString = "shit";

		ScopeTestUtil.setConversationIdsOnRequest(request);
		
		ActionProxy proxy = this.getActionProxy("/conversation/do1");
		
		ScopeTestUtil.extractScopeFields(this);
		
		String result = proxy.execute();
		
		assertEquals(Action.SUCCESS, result);
		
		ScopeTestUtil.injectScopeFields(this);

		System.out.println(conversationString);
		System.out.println(chubby);
	}

	@Test
	public void testBeforeInvocation() throws Exception {

		this.getActionProxy("/conversation/begin").execute();
		String id1 = ConversationUtil.getConversationId(CONVERSATION_NAME);

		this.getActionProxy("/conversation/begin").execute();
		String id2 = ConversationUtil.getConversationId(CONVERSATION_NAME);

		request.addParameter(CONVERSATION_NAME, id1);
		request.addParameter(CONVERSATION_FIELD, "one");
		this.getActionProxy("/conversation/do1").execute();

		request.addParameter(CONVERSATION_NAME, id2);
		request.addParameter(CONVERSATION_FIELD, "two");
		this.getActionProxy("/conversation/do1").execute();

		request.addParameter(CONVERSATION_NAME, id1);
		this.getActionProxy("/conversation/do2").execute();
		assertEquals("one", this.getAction().getConversationString());

		request.addParameter(CONVERSATION_NAME, id2);
		this.getActionProxy("/conversation/do2").execute();
		assertEquals("two", this.getAction().getConversationString());

		request.addParameter(CONVERSATION_NAME, id1);
		this.getActionProxy("/conversation/end").execute();

		request.addParameter(CONVERSATION_NAME, id1);
		this.getActionProxy("/conversation/do2").execute();
		assertEquals("initialState", this.getAction().getConversationString());

	}

	@Test
	public void testCrossActionWorkflowFieldPersistence() throws Exception {
		// TODO
	}

	@Test
	public void testActionWithNoWorkflows() throws Exception {
		// TODO
	}

}
