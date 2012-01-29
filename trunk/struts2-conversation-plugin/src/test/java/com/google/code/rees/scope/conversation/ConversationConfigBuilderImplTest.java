package com.google.code.rees.scope.conversation;

import static org.junit.Assert.assertNotNull;

import java.util.Collection;
import java.util.Map;

import org.junit.Test;

import com.google.code.rees.scope.conversation.ConversationConfiguration;
import com.google.code.rees.scope.conversation.ConversationConfigBuilder;
import com.google.code.rees.scope.struts2.StrutsScopeConstants;
import com.google.code.rees.scope.struts2.test.StrutsSpringScopeTestCase;
import com.google.code.struts2.test.junit.StrutsConfiguration;
import com.opensymphony.xwork2.inject.Inject;

@StrutsConfiguration(locations = "struts.xml")
public class ConversationConfigBuilderImplTest extends StrutsSpringScopeTestCase<Object> {
	
	@Inject(value=StrutsScopeConstants.CONVERSATION_CONFIG_BUILDER_KEY)
	ConversationConfigBuilder builder;
	
	@Test
	public void testGetConversationConfig() {
		
		Map<Class<?>, Collection<ConversationConfiguration>> configs = builder.getConversationConfigs();
		assertNotNull(configs);
		//TODO
	}
	
	@Test
	public void testConversationNaming() throws Exception {
		
		Map<Class<?>, Collection<ConversationConfiguration>> configs = builder.getConversationConfigs();
		assertNotNull(configs);
		//TODO
	}

}
