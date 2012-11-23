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
 *  $Id: StrutsActionProvider.java reesbyars $
 ******************************************************************************/
package com.google.code.rees.scope.struts2;

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.StrutsConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.code.rees.scope.ActionProvider;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.FileManager;
import com.opensymphony.xwork2.FileManagerFactory;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.TextParseUtil;
import com.opensymphony.xwork2.util.classloader.ReloadingClassLoader;
import com.opensymphony.xwork2.util.finder.ClassFinder;
import com.opensymphony.xwork2.util.finder.ClassFinder.ClassInfo;
import com.opensymphony.xwork2.util.finder.ClassLoaderInterface;
import com.opensymphony.xwork2.util.finder.ClassLoaderInterfaceDelegate;
import com.opensymphony.xwork2.util.finder.Test;
import com.opensymphony.xwork2.util.finder.UrlSet;

/**
 * Struts2 implementation of the {@link ActionProvider}.
 * 
 * @author rees.byars with code from the struts2 convention package
 */
public class StrutsActionProvider implements ActionProvider {

    private static final long serialVersionUID = 6728107973559862449L;

    private static final Logger LOG = LoggerFactory.getLogger(StrutsActionProvider.class);

    private Set<Class<?>> actionClasses;
    private String[] actionPackages;
    private String[] packageLocators;
    private String[] includeJars;
    private boolean disablePackageLocatorsScanning;
    private boolean checkImplementsAction;
    private String packageLocatorsBasePackage;
    private String actionSuffix;
    private Set<String> fileProtocols;
    private boolean devMode;
    private ReloadingClassLoader reloadingClassLoader;
    private boolean reload;
    private boolean excludeParentClassLoader;
    private boolean requireFollowsConvention;
    private FileManager fileManager;
    private Container container;

    public Set<Class<?>> getActionClasses() throws Exception {
    	try {
    		if (this.fileManager == null) {
    			//retrieve this way instead of by injection in order to catch an handle errors/exceptions with older versions of struts2
    			this.fileManager = this.container.getInstance(FileManagerFactory.class).getFileManager();
    		}
    		if (actionClasses == null) {
                initReloadClassLoader();
                actionClasses = this.findActions();
            }
            return this.actionClasses;
            //This is a hack to make sure that changes to Struts2 classes used below don't blow up the whole plugin
    	} catch (Throwable t) {
    		throw new Exception("Could not load action classes on startup.  Configuration caches will be built on-demand.");
    	}
        
    }
    
    @Inject
    public void setContainer(Container container) {
        this.container = container;
    }

    @Inject(StrutsConstants.STRUTS_DEVMODE)
    public void setDevMode(String mode) {
        this.devMode = "true".equals(mode);
    }

    /**
     * Reload configuration when classes change. Defaults to "false" and should
     * not be used in production.
     */
    @Inject(ConventionConstants.RELOAD_CLASSES)
    public void setReload(String reload) {
        this.reload = "true".equals(reload);
    }

    /**
     * Exclude URLs found by the parent class loader. Defaults to "true", set to
     * true for JBoss
     */
    @Inject(ConventionConstants.EXCLUDE_PARENT_CLASS_LOADER)
    public void setExcludeParentClassLoader(String exclude) {
        this.excludeParentClassLoader = "true".equals(exclude);
    }

    /**
     * File URLs whose protocol are in these list will be processed as jars
     * containing classes
     * 
     * @param fileProtocols
     *        Comma separated list of file protocols that will be considered
     *        as jar files and scanned
     */
    @Inject(ConventionConstants.FILE_PROTOCOLS)
    public void setFileProtocols(String fileProtocols) {
        if (StringUtils.isNotBlank(fileProtocols)) {
            this.fileProtocols = TextParseUtil
                    .commaDelimitedStringToSet(fileProtocols);
        }
    }

    /**
     * @param includeJars
     *        Comma separated list of regular expressions of jars to be
     *        included.
     */
    @Inject(value = ConventionConstants.INCLUDE_JARS, required = false)
    public void setIncludeJars(String includeJars) {
        if (StringUtils.isNotEmpty(includeJars))
            this.includeJars = includeJars.split("\\s*[,]\\s*");
    }

    /**
     * @param disablePackageLocatorsScanning
     *        If set to true, only the named packages will be scanned
     */
    @Inject(
            value = ConventionConstants.PACKAGE_LOCATORS_DISABLE,
            required = false)
    public void setDisablePackageLocatorsScanning(
            String disablePackageLocatorsScanning) {
        this.disablePackageLocatorsScanning = "true"
                .equals(disablePackageLocatorsScanning);
    }

    /**
     * @param actionPackages
     *        (Optional) An optional list of action packages that this
     *        should create configuration for.
     */
    @Inject(value = ConventionConstants.ACTION_PACKAGES, required = false)
    public void setActionPackages(String actionPackages) {
        if (StringUtils.isNotBlank(actionPackages)) {
            this.actionPackages = actionPackages.split("\\s*[,]\\s*");
        }
    }

    /**
     * @param checkImplementsAction
     *        (Optional) Map classes that implement
     *        com.opensymphony.xwork2.Action as actions
     */
    @Inject(
            value = ConventionConstants.CHECK_IMPLEMENTS_ACTION,
            required = false)
    public void setCheckImplementsAction(String checkImplementsAction) {
        this.checkImplementsAction = "true".equals(checkImplementsAction);
    }

    /**
     * @param actionSuffix
     *        (Optional) Classes that end with these value will be mapped as
     *        actions (defaults to "Action")
     */
    @Inject(value = ConventionConstants.ACTION_SUFFIX, required = false)
    public void setActionSuffix(String actionSuffix) {
        if (StringUtils.isNotBlank(actionSuffix)) {
            this.actionSuffix = actionSuffix;
        }
    }

    /**
     * @param packageLocators
     *        (Optional) A list of names used to find action packages.
     */
    @Inject(value = ConventionConstants.PACKAGE_LOCATORS, required = false)
    public void setPackageLocators(String packageLocators) {
        this.packageLocators = packageLocators.split("\\s*[,]\\s*");
    }

    /**
     * @param packageLocatorsBasePackage
     *        (Optional) If set, only packages that start with this name
     *        will be scanned for actions.
     */
    @Inject(
            value = ConventionConstants.PACKAGE_LOCATORS_BASE_PACKAGE,
            required = false)
    public void setPackageLocatorsBase(String packageLocatorsBasePackage) {
        this.packageLocatorsBasePackage = packageLocatorsBasePackage;
    }

    /**
     * @param requireFollowsConvention
     *        If true, only classes that follow the convention for action
     *        classes will
     *        be scanned for the scope annotations.
     */
    @Inject(value = StrutsScopeConstants.REQUIRE_FOLLOWS_CONVENTION)
    public void setRequireFollowsConvention(String requireFollowsConvention) {
        this.requireFollowsConvention = "true".equals(requireFollowsConvention);
    }

    /**
     * Note that we can't include the test for {@link #actionSuffix} here
     * because a class is included if its name ends in {@link #actionSuffix} OR
     * it implements {@link com.opensymphony.xwork2.Action}. Since the whole
     * goal is to avoid loading the class if we don't have to, the (actionSuffix
     * || implements Action) test will have to remain until later. See
     * {@link #getActionClassTest()} for the test performed on the loaded
     * {@link ClassInfo} structure.
     * 
     * @param className
     *        the name of the class to test
     * @return true if the specified class should be included in the
     *         package-based action scan
     */
    protected boolean includeClassNameInActionScan(String className) {

        String classPackageName = StringUtils.substringBeforeLast(className,
                ".");

        if (actionPackages != null) {
            for (String packageName : actionPackages) {
                String strictPackageName = packageName + ".";
                if (classPackageName.equals(packageName)
                        || classPackageName.startsWith(strictPackageName))
                    return true;
            }
        }

        if (packageLocators != null && !disablePackageLocatorsScanning) {
            for (String packageLocator : packageLocators) {
                if (classPackageName.length() > 0
                        && (packageLocatorsBasePackage == null || classPackageName
                                .startsWith(packageLocatorsBasePackage))) {
                    String[] splitted = classPackageName.split("\\.");

                    if (contains(splitted, packageLocator, false))
                        return true;
                }
            }
        }

        return false;
    }

    /**
     * Construct a {@link Test} object that determines if a specified class name
     * should be included in the package scan based on the clazz's package name.
     * Note that the goal is to avoid loading the class, so the test should only
     * rely on information in the class name itself. The default implementation
     * is to return the result of {@link #includeClassNameInActionScan(String)}.
     * 
     * @return a {@link Test} object that returns true if the specified class
     *         name should be included in the package scan
     */
    protected Test<String> getClassPackageTest() {
        return new Test<String>() {
            public boolean test(String className) {
                return includeClassNameInActionScan(className);
            }
        };
    }

    /**
     * Construct a {@link Test} Object that determines if a specified class
     * should be included in the package scan based on the full
     * {@link ClassInfo} of the class. At this point, the class has been loaded,
     * so it's ok to perform tests such as checking annotations or looking at
     * interfaces or super-classes of the specified class.
     * 
     * @return a {@link Test} object that returns true if the specified class
     *         should be included in the package scan
     */
    protected Test<ClassFinder.ClassInfo> getActionClassTest() {
        return new Test<ClassFinder.ClassInfo>() {
            public boolean test(ClassFinder.ClassInfo classInfo) {

                // Why do we call includeClassNameInActionScan here, when it's
                // already been called to in the initial call to ClassFinder?
                // When some action class passes our package filter in that
                // step,
                // ClassFinder automatically includes parent classes of that
                // action,
                // such as com.opensymphony.xwork2.ActionSupport. We repeat the
                // package filter here to filter out such results.
                boolean inPackage = includeClassNameInActionScan(classInfo
                        .getName());
                boolean nameMatches = classInfo.getName()
                        .endsWith(actionSuffix) || !requireFollowsConvention;

                try {
                    return inPackage
                            && (nameMatches || (checkImplementsAction && com.opensymphony.xwork2.Action.class
                                    .isAssignableFrom(classInfo.get())));
                } catch (ClassNotFoundException ex) {
                    if (LOG.isErrorEnabled())
                        LOG.error("Unable to load class [#0]", ex,
                                classInfo.getName());
                    return false;
                }
            }
        };
    }

    protected Set<Class<?>> findActions() {
        Set<Class<?>> classes = new HashSet<Class<?>>();
        try {
            if (actionPackages != null
                    || (packageLocators != null && !disablePackageLocatorsScanning)) {

                // By default, ClassFinder scans EVERY class in the specified
                // url set, which can produce spurious warnings for non-action
                // classes that can't be loaded. We pass a package filter that
                // only considers classes that match the action packages
                // specified by the user
                Test<String> classPackageTest = getClassPackageTest();
                ClassFinder finder = new ClassFinder(getClassLoaderInterface(),
                        buildUrlSet().getUrls(), true, this.fileProtocols,
                        classPackageTest);

                Test<ClassFinder.ClassInfo> test = getActionClassTest();
                for (Class<?> clazz : finder.findClasses(test)) {
                    classes.add(clazz);
                }
            }
        } catch (Exception ex) {
            if (LOG.isErrorEnabled())
                LOG.error("Unable to scan named packages", ex);
        }

        return classes;
    }

    protected boolean isReloadEnabled() {
        return devMode && reload;
    }

    protected void initReloadClassLoader() {
        // when the configuration is reloaded, a new classloader will be setup
        if (isReloadEnabled() && reloadingClassLoader == null)
            reloadingClassLoader = new ReloadingClassLoader(getClassLoader());
    }

    protected ClassLoaderInterface getClassLoaderInterface() {
        if (isReloadEnabled())
            return new ClassLoaderInterfaceDelegate(this.reloadingClassLoader);
        else {
            /*
             * if there is a ClassLoaderInterface in the context, use it,
             * otherwise default to the default ClassLoaderInterface (a wrapper
             * around the current thread classloader) using this, other plugins
             * (like OSGi) can plugin their own classloader for a while and it
             * will be used by Convention (it cannot be a bean, as Convention is
             * likely to be called multiple times, and it needs to use the
             * default ClassLoaderInterface during normal startup)
             */
            ClassLoaderInterface classLoaderInterface = null;
            ActionContext ctx = ActionContext.getContext();
            if (ctx != null)
                classLoaderInterface = (ClassLoaderInterface) ctx
                        .get(ClassLoaderInterface.CLASS_LOADER_INTERFACE);

            return (ClassLoaderInterface) ObjectUtils.defaultIfNull(
                    classLoaderInterface, new ClassLoaderInterfaceDelegate(
                            getClassLoader()));
        }
    }

    protected ClassLoader getClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    private UrlSet buildUrlSet() throws IOException {
        ClassLoaderInterface classLoaderInterface = getClassLoaderInterface();
        UrlSet urlSet = new UrlSet(fileManager, classLoaderInterface, this.fileProtocols);

        //excluding the urls found by the parent class loader is desired, but fails in JBoss (all urls are removed)
        if (excludeParentClassLoader) {
            //exclude parent of classloaders
            ClassLoaderInterface parent = classLoaderInterface.getParent();
            //if reload is enabled, we need to step up one level, otherwise the UrlSet will be empty
            //this happens because the parent of the realoding class loader is the web app classloader
            if (parent != null && isReloadEnabled())
                parent = parent.getParent();

            if (parent != null)
                urlSet = urlSet.exclude(parent);

            try {
                // This may fail in some sandboxes, ie GAE
                ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
                urlSet = urlSet.exclude(new ClassLoaderInterfaceDelegate(systemClassLoader.getParent()));

            } catch (SecurityException e) {
                if (LOG.isWarnEnabled())
                    LOG.warn("Could not get the system classloader due to security constraints, there may be improper urls left to scan");
            }
        }

        //try to find classes dirs inside war files
        urlSet = urlSet.includeClassesUrl(classLoaderInterface);


        urlSet = urlSet.excludeJavaExtDirs();
        urlSet = urlSet.excludeJavaEndorsedDirs();
        try {
        	urlSet = urlSet.excludeJavaHome();
        } catch (NullPointerException e) {
        	// This happens in GAE since the sandbox contains no java.home directory
            if (LOG.isWarnEnabled())
        	    LOG.warn("Could not exclude JAVA_HOME, is this a sandbox jvm?");
        }
        urlSet = urlSet.excludePaths(System.getProperty("sun.boot.class.path", ""));
        urlSet = urlSet.exclude(".*/JavaVM.framework/.*");

        if (includeJars == null) {
            urlSet = urlSet.exclude(".*?\\.jar(!/|/)?");
        } else {
            //jar urls regexes were specified
            List<URL> rawIncludedUrls = urlSet.getUrls();
            Set<URL> includeUrls = new HashSet<URL>();
            boolean[] patternUsed = new boolean[includeJars.length];

            for (URL url : rawIncludedUrls) {
                if (fileProtocols.contains(url.getProtocol())) {
                    //it is a jar file, make sure it macthes at least a url regex
                    for (int i = 0; i < includeJars.length; i++) {
                        String includeJar = includeJars[i];
                        if (Pattern.matches(includeJar, url.toExternalForm())) {
                            includeUrls.add(url);
                            patternUsed[i] = true;
                            break;
                        }
                    }
                } else {
                    //it is not a jar
                    includeUrls.add(url);
                }
            }

            if (LOG.isWarnEnabled()) {
                for (int i = 0; i < patternUsed.length; i++) {
                    if (!patternUsed[i]) {
                        LOG.warn("The includeJars pattern [#0] did not match any jars in the classpath", includeJars[i]);
                    }
                }
            }
            return new UrlSet(fileManager, includeUrls);
        }

        return urlSet;
    }

    public static boolean contains(String[] strings, String value,
            boolean ignoreCase) {
        if (strings != null) {
            for (String string : strings) {
                if ((string.equals(value))
                        || ((ignoreCase) && (string.equalsIgnoreCase(value)))) {
                    return true;
                }
            }
        }
        return false;
    }

}
