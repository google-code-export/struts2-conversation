/***********************************************************************************************************************
 *
 * Struts2-Conversation-Plugin - An Open Source Conversation- and Flow-Scope Solution for Struts2-based Applications
 * =================================================================================================================
 *
 * Copyright (C) 2012 by Rees Byars
 * http://code.google.com/p/struts2-conversation/
 *
 ***********************************************************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 ***********************************************************************************************************************
 *
 * $Id: BasicTimeoutMonitorTest.java Apr 19, 2012 10:34:50 AM reesbyars $
 *
 **********************************************************************************************************************/
package com.google.code.rees.scope.util.monitor;

import org.junit.Test;

import com.google.code.rees.scope.conversation.ConversationUtil;
import com.google.code.rees.scope.conversation.context.ConversationContext;
import com.google.code.rees.scope.conversation.context.DefaultConversationContext;
import com.google.code.rees.scope.testutil.SerializationTestingUtil;

/**
 * @author rees.byars
 *
 */
public class BasicTimeoutMonitorTest {
	
	@Test
	public void testSerialization() {
		
		//TODO this is not a great test per se

		TimeoutMonitor<ConversationContext> m = BasicTimeoutMonitor.spawnInstance(100L);
		
		for (int i = 0; i < 500; i++) {
			m.addTimeoutable(new DefaultConversationContext("sf",
					ConversationUtil.generateId(), 5000000L));
			m.addTimeoutable(new DefaultConversationContext("sfff",
					ConversationUtil.generateId(), 5000000L));
			m.addTimeoutable(new DefaultConversationContext("sff",
					ConversationUtil.generateId(), 5000000L));
			m.addTimeoutable(new DefaultConversationContext("ssss",
					ConversationUtil.generateId(), 5000000L));
			m.addTimeoutable(new DefaultConversationContext("ssdddd",
					ConversationUtil.generateId(), 5000000L));
			m = SerializationTestingUtil.getSerializedCopy(m);
		}

		for (int ii = 0; ii < 300; ii++) {
			m.addTimeoutable(new DefaultConversationContext("s",
					ConversationUtil.generateId(), 50L));
			for (long iii = 0; iii < 10000000; iii++) {

			}
			Thread.yield();
		}

		m.destroy();
	}

}
