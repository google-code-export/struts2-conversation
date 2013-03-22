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
 *  $Id: ActionProvider.java reesbyars $
 ******************************************************************************/
package com.github.overengineer.scope;

import java.io.Serializable;
import java.util.Set;

/**
 * Provides a Set of action classes for initializing configurations.
 * 
 * @author rees.byars
 */
public interface ActionProvider extends Serializable {

    /**
     * Returns the set of action classes
     * 
     * @return
     */
    public Set<Class<?>> getActionClasses();

}
