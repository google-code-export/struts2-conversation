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
 *  $Id: NamingUtil.java reesbyars $
 ******************************************************************************/
package com.github.overengineer.scope.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Utility for converting camel-case names into dash-delimited names, e.g.
 * thisTypeOfName becomes this-type-of-name.
 * 
 * @author rees.byars
 */
public class NamingUtil {

    public static String getConventionName(Class<?> clazz, String suffixToRemove) {
        String conventionName = clazz.getSimpleName();
        int suffixIndex = conventionName.lastIndexOf(suffixToRemove);
        if (suffixIndex > 0) {
            conventionName = conventionName.substring(0, suffixIndex);
        }
        conventionName = getConventionName(conventionName);
        return conventionName;
    }

    public static String getConventionName(Class<?> clazz) {
        return getConventionName(clazz.getSimpleName());
    }

    public static String getConventionName(Method method) {
        return getConventionName(method.getName());
    }

    public static String getConventionName(Field field) {
        return getConventionName(field.getName());
    }

    public static String getConventionName(String camelCaseString) {
        String conventionName = camelCaseString;
        return conventionName.replaceAll(
                String.format("%s|%s|%s", "(?<=[A-Z])(?=[A-Z][a-z])",
                        "(?<=[^A-Z])(?=[A-Z])", "(?<=[A-Za-z])(?=[^A-Za-z])"),
                "-").toLowerCase();
    }

}
