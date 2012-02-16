package com.google.code.rees.scope.session;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Map;
import java.util.Set;

import org.junit.Test;

import com.google.code.rees.scope.mocks.actions.MockPojoController;
import com.google.code.rees.scope.session.SessionConfiguration;
import com.google.code.rees.scope.session.SessionConfigurationProvider;
import com.google.code.rees.scope.struts2.StrutsScopeConstants;
import com.google.code.rees.scope.struts2.test.StrutsScopeTestCase;
import com.google.code.rees.scope.testutil.TestConstants;
import com.google.code.struts2.test.junit.StrutsConfiguration;
import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.inject.Inject;

@StrutsConfiguration(locations = "struts.xml")
public class DefaultSessionConfigurationProviderTest extends StrutsScopeTestCase<Object> {
	
	@Inject(value=StrutsScopeConstants.SESSION_CONFIG_PROVIDER_KEY)
	SessionConfigurationProvider builder;
	
	@Test
	@SuppressWarnings("rawtypes")
	public void testGetSessionFieldConfig() {
		SessionConfiguration config = builder.getSessionConfiguration(MockPojoController.class);
		assertNotNull(config);
		for (Class clazz : TestConstants.SESSION_FIELD_ACTION_CLASSES) {
			String failMessage = "SessionConfigurationProvider failed to provide config for class:  " + clazz.getName();
			assertNotNull(failMessage, config.getFields(clazz));
		}
		for (Class clazz : TestConstants.NO_SESSION_FIELD_ACTION_CLASSES) {
			String failMessage = "SessionConfigurationProvider erroneously provided config for class:  " + clazz.getName();
			assertNull(failMessage, config.getFields(clazz));
		}
		
	}
	
	@Test
	public void testSessionFieldNaming() throws Exception {
		
		SessionConfiguration config = builder.getSessionConfiguration(MockPojoController.class);
		assertNotNull(config);
		Set<String> fieldNames = config.getFields(MockPojoController.class).keySet();
		assertTrue(fieldNames.contains("java.lang.String.sessionField"));

		//this next assertion isn't really related but i did it for the shit of it
		request.addParameter("sessionField", "hola");
		ActionProxy proxy = this.getActionProxy("begin");
		proxy.execute();
		
		@SuppressWarnings("unchecked")
		Map<String, Object> sessionFieldMap = (Map<String, Object>)session.get(StrutsScopeConstants.SESSION_FIELD_MAP_KEY);
		assertTrue(sessionFieldMap.containsKey("java.lang.String.sessionString"));
	}

}
