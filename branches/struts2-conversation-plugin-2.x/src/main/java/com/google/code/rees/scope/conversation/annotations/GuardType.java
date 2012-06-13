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
 * $Id: GuardType.java Jun 8, 2012 10:50:27 AM reesbyars $
 *
 **********************************************************************************************************************/
package com.google.code.rees.scope.conversation.annotations;

/**
 * 
 * This enum is used to designate the type of action guarding to apply to conversation actions.
 * Guarding is used to prevent actions from executing in the absence of valid Conversation IDs.  
 * An ID is valid only if it corresponds to an active conversation.  This can be used to prevent
 * users from generating odd or undesired behavior by executing conversation actions outside of
 * a conversation context.  
 * 
 * @author rees.byars
 */
public enum GuardType {
	
	/**
	 * With {@link GuardType#NONE}, conversation actions are not guarded at all.
	 */
	NONE, 
	/**
	 * With {@link GuardType#ALL}, the conversation action will be guarded for all of its conversations, i.e. 
	 * a valid conversation ID must be present on the request for all conversations of which the action
	 * is a member.
	 */
	ALL, 
	/**
	 * With {@link GuardType#SUBSET}, the conversation action will be guarded for a specified subset of its conversations, i.e. 
	 * a valid conversation ID must be present on the request for all conversations of which the action
	 * is a member and that are in the subset.
	 * <p>
	 * The subset may be specified using <code>guardedConversations</code> attribute on the conversation annotations.
	 */
	SUBSET;

}
