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
 *  $Id: ConversationUrlTag.java reesbyars $
 ******************************************************************************/
package com.google.code.rees.scope.struts2.ui;

import java.util.Map;

import org.apache.struts2.views.jsp.URLTag;

import com.google.code.rees.scope.struts2.StrutsScopeConstants;

/**
 * A normal Struts2 {@link URLTag}, but additionally includes the conversation
 * IDs as parameters on the URL
 * 
 * @author rees.byars
 */
public class ConversationUrlTag extends URLTag {

	private static final long serialVersionUID = -2799594627916112974L;

	@Override
	protected void populateParams() {
		super.populateParams();
		@SuppressWarnings("unchecked")
		Map<String, String> convoIdMap = (Map<String, String>) this.component.getStack().findValue(StrutsScopeConstants.CONVERSATION_ID_MAP_STACK_KEY);
		this.component.addAllParameters(convoIdMap);
	}

}
