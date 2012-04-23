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
 *  $Id: SessionConfiguration.java reesbyars $
 ******************************************************************************/
package com.google.code.rees.scope.session;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is used to cache the fields and action IDs for all
 * {@link SessionField SessionFields}.
 * 
 * @see {@link SessionConfigurationProvider}
 * @see {@link SessionManager}
 * 
 * @author rees.byars
 */
public class SessionConfiguration {

    private Map<Class<?>, Map<String, Field>> fields;

    public SessionConfiguration() {
        fields = new HashMap<Class<?>, Map<String, Field>>();
    }

    /**
     * Caches the given field in the configuration
     * 
     * @param clazz
     * @param name
     * @param field
     */
    public void addField(Class<?> clazz, String name, Field field) {
        Map<String, Field> classFields = fields.get(clazz);
        if (classFields == null) {
            classFields = new HashMap<String, Field>();
        }
        classFields.put(name, field);
        fields.put(clazz, classFields);
    }

    /**
     * Returns the cached fields for the given class
     * 
     * @param clazz
     * @return
     */
    public Map<String, Field> getFields(Class<?> clazz) {
        return fields.get(clazz);
    }
}
