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
 *  $Id: ModelDrivenConversationSupport.java reesbyars $
 ******************************************************************************/
package com.google.code.rees.scope.struts2;

import java.io.Serializable;

import com.google.code.rees.scope.conversation.ConversationUtil;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ModelDriven;

/**
 * This class makes it simple to manage models with conversation-scoped
 * life-cycles. It can be used in place of the
 * {@link com.google.code.rees.scope.conversation.annotations.ConversationController
 * ConversationController} annotation for people who hate annotations/meta-data.
 * 
 * All access to the model is through the {@link #getModel()} and
 * {@link #setModel(Serializable)} methods so that retrieval and insertion of
 * the model from and into conversation instances can be managed on behalf of
 * inheriting classes.
 * 
 * 
 * @author rees.byars
 * 
 * @param <T>
 */
public abstract class ModelDrivenConversationSupport<T extends Serializable> extends ActionSupport implements ModelDriven<T> {

	private static final long serialVersionUID = -8313905274280660299L;
	private T model;

	/**
	 * {@inheritDoc}
	 * 
	 * The model is scoped to the conversations associated with the current
	 * request and action.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public T getModel() {
		if (this.model == null) {
			this.model = (T) ConversationUtil.getField(this.getModelName());
		}
		return this.model;
	}

	/**
	 * Set the model. The model is scoped to the conversations associated with
	 * the current request and action.
	 * 
	 * @param model
	 */
	public void setModel(T model) {
		ConversationUtil.setField(this.getModelName(), model);
		this.model = model;
	}

	/**
	 * The name of the model used to identify it in the
	 * {@link com.google.code.rees.scope.conversation.context.ConversationContext
	 * ConversationContext}.
	 * 
	 * This can be overridden to provide the name of choice. The default is
	 * <code>this.getClass().getName()</code>.
	 * 
	 * @return
	 */
	protected String getModelName() {
		return this.getClass().getName();
	}

}
