/*******************************************************************************
 * 
 *  Struts2-Conversation-Plugin - An Open Source Conversation- and Flow-Scope Solution for Struts2-based Applications
 *  =================================================================================================================
 * 
 *  Copyright (C) 2012 by Rees Byars
 *  http://code.google.com/p/struts2-conversation/
 * 
 * **********************************************************************************************************************
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 *  the License. You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 *  an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 *  specific language governing permissions and limitations under the License.
 * 
 * **********************************************************************************************************************
 * 
 *  $Id: ConversationScope.java reesbyars $
 ******************************************************************************/
package com.google.code.rees.scope.spring;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;

import com.google.code.rees.scope.conversation.ConversationUtil;

/**
 * This custom scope must be configured in a Spring XML context. It allows for
 * conversation-scoped beans to be injected by the Spring IoC.
 * 
 * @author rees.byars
 */
public class ConversationScope implements Scope {

	@Override
	public Object get(String name, ObjectFactory<?> objectFactory) {
		Object object = ConversationUtil.getField(name);
		if (object == null) {
			object = objectFactory.getObject();
			ConversationUtil.setField(name, object);
		}
		return object;
	}

	@Override
	public String getConversationId() {
		return null;
	}

	@Override
	public void registerDestructionCallback(String name, Runnable destructionCallback) {
	}

	@Override
	public Object remove(String name) {
		return null;
	}

	@Override
	public Object resolveContextualObject(String name) {
		return null;
	}

}
