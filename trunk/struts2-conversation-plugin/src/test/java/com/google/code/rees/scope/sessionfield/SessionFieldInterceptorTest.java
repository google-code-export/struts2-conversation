package com.google.code.rees.scope.sessionfield;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.google.code.rees.scope.mocks.actions.MockConventionController;
import com.google.code.rees.scope.mocks.actions.MockPojoController;
import com.google.code.rees.scope.testutil.ScopeTestCase;
import com.opensymphony.xwork2.ActionProxy;

public class SessionFieldInterceptorTest extends ScopeTestCase<MockConventionController> {
	
	@Test
	public void testBeforeInvocation() throws Exception {
		
		request.setParameter("abstractField", "hello");
		ActionProxy proxy = this.getActionProxy("begin");
		assertEquals("hola", this.getAction().getAbstractField());
		assertEquals(2, this.getAction().getDumb());
		assertEquals(2, this.getAction().getSmart());
		assertEquals(MockConventionController.NOT_IN_PROGRESS, this.getAction().getSessionString());
 		proxy.execute();
		assertEquals("hello", this.getAction().getAbstractField());
		assertEquals(3, this.getAction().getDumb());
		assertEquals(3, this.getAction().getSmart());
		assertEquals(MockConventionController.IN_PROGRESS, this.getAction().getSessionString());
		this.getActionProxy("continue1").execute();
		this.getActionProxy("continue2").execute();
		assertEquals(2, this.getAction().getDumb());
		assertEquals(3, this.getAction().getSmart());
		assertEquals(MockConventionController.IN_PROGRESS, this.getAction().getSessionString());
		request.setParameter("smart", "145");
		this.getActionProxy("end").execute();
		assertEquals(MockConventionController.NOT_IN_PROGRESS, this.getAction().getSessionString());
		assertEquals(145, this.getAction().getSmart());
		assertEquals("hello", this.getAction().getAbstractField());
	}
	
	@Test
	public void testCrossActionSessionFieldPersistence() throws Exception {
		request.setParameter("sessionField", "test");
		this.getActionProxy("configuration-action").execute();
		this.getActionProxy("mock-pojo").execute();
		assertEquals("test", getAction(MockPojoController.class).getSessionField());
	}
	
	@Test
	public void testActionWithNoSessionFields() throws Exception {
		this.getActionProxy("mock-no-session-field").execute();
	}
	
}
