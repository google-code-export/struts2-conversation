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
 *  $Id: InjectionUtil.java reesbyars $
 ******************************************************************************/
package com.google.code.rees.scope.util;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A utility class that provides static methods that are used internally
 * for accessing cached fields reflectively.
 * 
 * @author rees.byars
 */
public class InjectionUtil {

    private static final Logger LOG = LoggerFactory.getLogger(InjectionUtil.class);

    public static Map<String, Object> getFieldValues(Object action, Map<String, Field> classScopeConfig) {

        Map<String, Object> scopedValues = new HashMap<String, Object>();
        for (Entry<String, Field> fieldEntry : classScopeConfig.entrySet()) {
            Field field = fieldEntry.getValue();
            String fieldName = fieldEntry.getKey();
            try {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Getting value from session for field with name " + fieldName);
                }
                Object value = field.get(action);
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Got value from session, toString() of this value is:  " + value);
                }
                scopedValues.put(fieldName, value);
            } catch (IllegalArgumentException e) {
                LOG.warn("Illegal argument exception while trying to obtain field named " + fieldName + " from action of class " + action.getClass());
            } catch (IllegalAccessException e) {
                LOG.warn("Illegal access exception while trying to obtain field named " + fieldName + " from action of class " + action.getClass());
            }
        }
        return scopedValues;
    }

    public static void setFieldValues(Object action, Map<String, Field> classScopeConfig, Map<String, Object> scopedValues) {

        for (Entry<String, Field> fieldEntry : classScopeConfig.entrySet()) {
            Field field = fieldEntry.getValue();
            String fieldName = fieldEntry.getKey();
            if (LOG.isDebugEnabled()) {
                LOG.debug("Setting field by the name of " + fieldName);
            }
            Object value = scopedValues.get(fieldName);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Setting field with value from session with toString() of " + value);
            }
            try {
                if (!(field.getType().isPrimitive() && value == null)) {
                    field.set(action, value);
                }
            } catch (IllegalAccessException ex) {
                LOG.warn("Illegal access exception while trying to set field named " + fieldName + " from action of class " + action.getClass());
            }
        }
    }
}
