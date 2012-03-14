package com.google.code.rees.scope.struts2;

import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.code.rees.scope.ActionProvider;
import com.google.code.rees.scope.struts2.StrutsConversationConstants;
import com.google.code.rees.scope.testutil.StrutsSpringScopeTestCase;
import com.google.code.rees.scope.testutil.TestConstants;
import com.google.code.struts2.test.junit.StrutsConfiguration;
import com.opensymphony.xwork2.inject.Inject;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:*applicationContext.xml")
public class StrutsActionProviderTest extends StrutsSpringScopeTestCase<Object> {
	
	@Inject(value=StrutsConversationConstants.ACTION_FINDER_KEY)
	ActionProvider finder;

	/**
	 * tests the case of: <br/><code>&ltconstant name="struts.sessionfield.followsConvention" value="false"/></code>
	 * @throws Exception
	 */
	@Test
	@StrutsConfiguration(locations = "struts-configuration.xml")
	public void testFindingAllActions() throws Exception {
		
		Set<Class<?>> actionClasses = finder.getActionClasses();
		for (Class<?> clazz : TestConstants.ALL_ACTION_CLASSES) {
			String failMessage = "StrutsActionProvider failed to find class:  " + clazz.getName();
			Assert.assertTrue(failMessage, actionClasses.contains(clazz));
		}
		Assert.assertEquals("Too many action classes found:  ", TestConstants.ALL_ACTION_CLASSES.size(), actionClasses.size());
	}
	
	/**
	 * tests the case of: <br/><code>&ltconstant name="struts.sessionfield.followsConvention" value="true"/></code>
	 * @throws Exception
	 */
	@Test
	@StrutsConfiguration(locations = "struts-convention.xml")
	public void testFindConventionActionsOnly() throws Exception {

		Set<Class<?>> actionClasses = finder.getActionClasses();
		for (Class<?> clazz : TestConstants.ALL_CONVENTION_ACTION_CLASSES) {
			String failMessage = "StrutsActionProvider failed to find class:  " + clazz.getName();
			Assert.assertTrue(failMessage, actionClasses.contains(clazz));
		}
		Assert.assertEquals("Too many action classes found:  ", TestConstants.ALL_CONVENTION_ACTION_CLASSES.size(), actionClasses.size());
	}
	
}
