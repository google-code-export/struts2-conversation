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
 * $Id: ActionUtil.java Apr 27, 2012 11:17:43 PM reesbyars $
 *
 **********************************************************************************************************************/
package com.github.overengineer.scope.util;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Set;
import java.util.TreeSet;


/**
 * @author rees.byars
 *
 */
public class ActionUtil {

	/**
	 * 
	 * @param method
	 * @return true if the method signature matches appears to be a Struts2 action method signature
	 */
	public static boolean isAction(Method method) {
        String methodName = method.getName();
        return (!(methodName.startsWith("get") || methodName.startsWith("set") || methodName
                .startsWith("is"))
                && Modifier.isPublic(method.getModifiers())
                && !Modifier.isStatic(method.getModifiers())
                && method.getReturnType().equals(String.class) && method
                .getParameterTypes().length == 0);
    }
	
	/**
	 * makes a best effort using {@link #isAction(Method)} to return all the action methods
	 * from the given class - if the class does not follow conventions, then this may not
	 * be an accurate set!  Follow conventions!
	 * 
	 * @param actionClass
	 * @return
	 */
	public static Set<String> getActions(Class<?> actionClass) {
		Set<String> actionNames = new TreeSet<String>();
        for (Method method : ReflectionUtil.getMethods(actionClass)) {
            if (ActionUtil.isAction(method)) {
                actionNames.add(method.getName());
            }
        }
        return actionNames;
	}
	
}
