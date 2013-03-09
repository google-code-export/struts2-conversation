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
 *  $Id: ConventionConstants.java reesbyars $
 ******************************************************************************/
package com.github.overengineer.scope.struts2;

/**
 * Constants from the convention plugin. Used for injection.
 * 
 * @author rees.byars
 */
public interface ConventionConstants {

    public final static String RELOAD_CLASSES = "struts.convention.classes.reload";
    public final static String EXCLUDE_PARENT_CLASS_LOADER = "struts.convention.exclude.parentClassLoader";
    public final static String FILE_PROTOCOLS = "struts.convention.action.fileProtocols";
    public final static String INCLUDE_JARS = "struts.convention.action.includeJars";
    public final static String PACKAGE_LOCATORS_DISABLE = "struts.convention.package.locators.disable";
    public final static String ACTION_PACKAGES = "struts.convention.action.packages";
    public final static String CHECK_IMPLEMENTS_ACTION = "struts.convention.action.checkImplementsAction";
    public final static String ACTION_SUFFIX = "struts.convention.action.suffix";
    public final static String PACKAGE_LOCATORS = "struts.convention.package.locators";
    public final static String PACKAGE_LOCATORS_BASE_PACKAGE = "struts.convention.package.locators.basePackage";

}
