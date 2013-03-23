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
package com.github.overengineer.scope.session;

import com.github.overengineer.scope.bijection.Bijector;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * This class is used to cache the fields and action IDs for all
 * {@link SessionField SessionFields}.
 *
 * @author rees.byars
 * @see {@link SessionConfigurationProvider}
 * @see {@link SessionManager}
 */
public class SessionConfiguration {

    private Map<Class<?>, Set<Bijector>> bijectorCache = new HashMap<Class<?>, Set<Bijector>>();

    /**
     *
     * @param clazz
     * @param bijector
     */
    public void addBijector(Class<?> clazz, Bijector bijector) {
        Set<Bijector> bijectors = bijectorCache.get(clazz);
        if (bijectors == null) {
            bijectors = new HashSet<Bijector>();
            bijectorCache.put(clazz, bijectors);
        }
        bijectors.add(bijector);
    }

    /**
     *
     * @param clazz
     * @return
     */
    public Set<Bijector> getBijectors(Class<?> clazz) {
        return bijectorCache.get(clazz);
    }
}
