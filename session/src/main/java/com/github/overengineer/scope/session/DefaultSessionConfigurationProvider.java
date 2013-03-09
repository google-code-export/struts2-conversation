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
 *  $Id: DefaultSessionConfigurationProvider.java reesbyars $
 ******************************************************************************/
package com.github.overengineer.scope.session;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.code.rees.scope.util.ReflectionUtil;

/**
 * 
 * The default implementation of the {@link SessionConfigurationProvider}.
 * 
 * @author rees.byars
 */
public class DefaultSessionConfigurationProvider implements SessionConfigurationProvider {

    private static final long serialVersionUID = 3703684121698557461L;
    private static final Logger LOG = LoggerFactory.getLogger(DefaultSessionConfigurationProvider.class);
    protected transient SessionConfiguration configuration = new SessionConfiguration();
    protected Set<Class<?>> classesProcessed = new HashSet<Class<?>>();

    /**
     * {@inheritDoc}
     */
    @Override
    public void init(Set<Class<?>> classes) {
        this.processClasses(classes);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SessionConfiguration getSessionConfiguration(Class<?> clazz) {
        if (this.configuration == null) {
            synchronized (this.classesProcessed) {
                this.initConfig();
            }
        }
        if (!this.classesProcessed.contains(clazz)) {
            this.processClass(clazz);
        }
        return this.configuration;
    }

    protected synchronized void initConfig() {
        if (this.configuration == null) {
            this.configuration = new SessionConfiguration();
            this.processClasses(this.classesProcessed);
        }
    }

    protected void processClasses(Set<Class<?>> classes) {
        for (Class<?> clazz : classes) {
            this.processClass(clazz);
        }
    }

    protected synchronized void processClass(Class<?> clazz) {
        if (!this.classesProcessed.contains(clazz)) {
            if (!clazz.isInterface()
                    && !Modifier.isAbstract(clazz.getModifiers())
                    && !clazz.isAnnotation()) {
                LOG.info("Loading @SessionField fields from " + clazz.getName());
                addFields(clazz);
                this.classesProcessed.add(clazz);
            }
        }
    }

    protected void addFields(Class<?> clazz) {
        for (Field field : ReflectionUtil.getFields(clazz)) {
            if (field.isAnnotationPresent(SessionField.class)) {
                LOG.debug("Adding @SessionField " + field.getName() + " from class " + clazz.getName() + " to the SessionConfiguration");
                SessionField sessionField = (SessionField) field.getAnnotation(SessionField.class);
                String name = sessionField.name();
                if (name.equals(SessionField.DEFAULT)) {
                    name = field.getName();
                }
                String key = SessionUtil.buildKey(name, field.getType());
                ReflectionUtil.makeAccessible(field);
                this.configuration.addField(clazz, key, field);
            }
        }
    }

}
