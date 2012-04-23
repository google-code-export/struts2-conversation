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
 *  $Id: ProgrammaticModelDrivenConversation.java reesbyars $
 ******************************************************************************/
package com.google.code.rees.scope.struts2.programmatic;

import java.io.Serializable;

import com.opensymphony.xwork2.ModelDriven;

/**
 * An interface for simplifying the integration of the {@link ModelDriven}
 * interface
 * and pattern with the programmatic management of the conversation life-cycle.
 * 
 * In most cases, extending the ProgrammaticModelDrivenConversationSupport
 * should
 * be used in preference to directly implementing this interface.
 * 
 * @author rees.byars
 * 
 * @param <T>
 */
public interface ProgrammaticModelDrivenConversation<T extends Serializable>
        extends ModelDriven<T> {

    /**
     * Set the model instance.
     * 
     * @param model
     */
    public void setModel(T model);

    /**
     * The conversations for which this class is a member.
     * 
     * @return
     */
    public String[] getConversations();

}
