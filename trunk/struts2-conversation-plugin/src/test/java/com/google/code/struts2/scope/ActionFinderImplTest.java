package com.google.code.struts2.scope;

import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import com.google.code.struts2.scope.ActionFinder;
import com.google.code.struts2.scope.ScopeConstants;
import com.google.code.struts2.scope.testutil.ScopeTestCase;
import com.google.code.struts2.scope.testutil.TestConstants;
import com.google.code.struts2.test.junit.StrutsConfiguration;
import com.opensymphony.xwork2.inject.Inject;

public class ActionFinderImplTest extends ScopeTestCase<Object> {
	
	@Inject(value=ScopeConstants.ACTION_FINDER_KEY)
	ActionFinder finder;

	/**
	 * tests the case of: <br/><code>&ltconstant name="struts.sessionfield.followsConvention" value="false"/></code>
	 * @throws Exception
	 */
	@Test
	@StrutsConfiguration(locations = "struts-configuration.xml")
	public void testFindingAllActions() throws Exception {
		
		Set<Class<?>> actionClasses = finder.getActionClasses();
		for (Class<?> clazz : TestConstants.ALL_ACTION_CLASSES) {
			String failMessage = "ActionFinderImpl failed to find class:  " + clazz.getName();
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
			String failMessage = "ActionFinderImpl failed to find class:  " + clazz.getName();
			Assert.assertTrue(failMessage, actionClasses.contains(clazz));
		}
		Assert.assertEquals("Too many action classes found:  ", TestConstants.ALL_CONVENTION_ACTION_CLASSES.size(), actionClasses.size());
	}
	
}
