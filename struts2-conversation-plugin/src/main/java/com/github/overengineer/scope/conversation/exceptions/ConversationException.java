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
 * $Id: ConversationException.java Jun 3, 2012 5:05:20 PM reesbyars $
 *
 **********************************************************************************************************************/
package com.github.overengineer.scope.conversation.exceptions;

/**
 * An exception thrown when exceptions occur in conversation processing.  {@link com.github.overengineer.scope.conversation.processing.ConversationProcessor 
 * ConversationProcessor's} should catch any other exceptions and wrap them with this class to allow for simple
 * exception mapping for any conversation-related exceptions.  
 * 
 * @author rees.byars
 *
 */
public class ConversationException extends Exception {

	private static final long serialVersionUID = 9175214621610696767L;
	
	public ConversationException(String message) {
		super(message);
	}

}
