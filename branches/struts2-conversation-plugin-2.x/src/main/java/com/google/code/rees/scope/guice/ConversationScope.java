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
package com.google.code.rees.scope.guice;

import com.google.code.rees.scope.conversation.ConversationUtil;
import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.Scope;

/**
 * @author rees.byars
 */
public class ConversationScope implements Scope {

	@Override
	public <T> Provider<T> scope(final Key<T> key, final Provider<T> provider) {

		return new Provider<T>() {

			public T get() {

				String contextKey = String.valueOf(key.hashCode());

				@SuppressWarnings("unchecked")
				T value = (T) ConversationUtil.getField(contextKey);

				if (value == null) {
					value = provider.get();
					ConversationUtil.setField(contextKey, value);
				}

				return value;
			}
		};
	}

}
