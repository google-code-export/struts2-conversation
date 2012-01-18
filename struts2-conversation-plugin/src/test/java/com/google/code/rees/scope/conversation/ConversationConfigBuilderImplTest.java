package com.google.code.rees.scope.conversation;

import static org.junit.Assert.assertNotNull;

import java.util.Collection;
import java.util.Map;

import org.junit.Test;

import com.google.code.rees.scope.conversation.ConversationConfig;
import com.google.code.rees.scope.conversation.ConversationConfigBuilder;
import com.google.code.rees.scope.struts2.StrutsScopeConstants;
import com.google.code.rees.scope.testutil.ScopeTestCase;
import com.google.code.struts2.test.junit.StrutsConfiguration;
import com.opensymphony.xwork2.inject.Inject;

@StrutsConfiguration(locations = "struts.xml")
public class ConversationConfigBuilderImplTest extends ScopeTestCase<Object> {
	
	@Inject(value=StrutsScopeConstants.CONVERSATION_CONFIG_BUILDER_KEY)
	ConversationConfigBuilder builder;
	
	@Test
	public void testGetConversationConfig() {
		
		Map<Class<?>, Collection<ConversationConfig>> configs = builder.getConversationConfigs();
		assertNotNull(configs);
		//TODO
	}
	
	@Test
	public void testConversationNaming() throws Exception {
		
		Map<Class<?>, Collection<ConversationConfig>> configs = builder.getConversationConfigs();
		assertNotNull(configs);
		//TODO
	}

}
