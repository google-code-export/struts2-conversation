package com.github.overengineer.container;

import com.github.overengineer.container.dynamic.DynamicComponentFactory;
import com.github.overengineer.container.key.*;
import com.github.overengineer.container.metadata.MetadataAdapter;
import com.github.overengineer.container.module.InstanceMapping;
import com.github.overengineer.container.module.Mapping;
import com.github.overengineer.container.module.Module;
import com.github.overengineer.container.scope.Scope;
import com.github.overengineer.container.scope.Scopes;
import com.github.overengineer.container.util.ParameterRef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.*;

/**
 *
 * TODO Features:
 * TODO scoped containers and proxies, factories, enhance interceptor rules (@OR, @AND, @NOT)
 * TODO named containers
 * TODO for scoped proxies - new proxy that uses a provider to obtain it's component on each request -
 * TODO       new ThreadLocalContainer interface and DelegatingThreadLocalContainer impl
 * TODO       new ThreadLocalContainerStrategy, so new DelegatingThreadLocalContainer(threadLocalStrategy);
 * TODO       for instance:
 *
 * <pre>
 *
 *     class SessionScopedContainerStrategy implements ThreadLocalContainerStrategy {
 *
 *          SessionScopedContainerStrategy(Module ... sessionModules) {
 *
 *          }
 *
 *          public Container getContainer(String name?) {
 *              HttpServletRequest request = RequestHolder.getRequest();
 *              HttpSession session = request.getSession(true);
 *              Container = session.get("session.scoped.container." + name);
 *              if (container == null) {
 *                  container = Clarence.please.gimmeThatTainer(sessionModules);
 *                  session.put(...
 *              }
 *              return container;
 *          }
 *
 *     }
 *
 *
 * </pre>
 *
 *
 *  <pre>
 *
 *     class MyApplication extends WebApplication {
 *
 *
 *          protected void configure() {
 *
 *              requestScope.loadModule(...
 *
 *              sessionScope.loadModule(...
 *
 *              globalScope.loadModule(...
 *
 *              master.loadModule(...
 *
 *
 *          }
 *
 *     }
 *
 *
 * </pre>
 *
 * TODO ScopedProxyHandler(ThreadLocalContainer container)
 * TODO
 *
 *
 * TODO Tech debt:
 * TODO cleanup interceptor impl, move from extensions to decorations?
 * TODO move aspect list to invocation factory, create new "aspect cache" interface for factory to implement?
 * TODO then the aop container can just interface with its cache and that of its children?
 *
 *
 * @author rees.byars
 */
public class DefaultContainer implements Container {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultContainer.class);

    private final Map<Key<?>, SortedSet<ComponentStrategy<?>>> strategies = new HashMap<Key<?>, SortedSet<ComponentStrategy<?>>>();
    private final List<Container> cascadingContainers = new ArrayList<Container>();
    private final List<Container> children = new ArrayList<Container>();
    private final ComponentStrategyFactory strategyFactory;
    private final DynamicComponentFactory dynamicComponentFactory;
    private final MetadataAdapter metadataAdapter;
    private final List<ComponentInitializationListener> componentInitializationListeners;

    public DefaultContainer(ComponentStrategyFactory strategyFactory, DynamicComponentFactory dynamicComponentFactory, MetadataAdapter metadataAdapter, List<ComponentInitializationListener> componentInitializationListeners) {
        this.strategyFactory = strategyFactory;
        this.dynamicComponentFactory = dynamicComponentFactory;
        this.metadataAdapter = metadataAdapter;
        this.componentInitializationListeners = componentInitializationListeners;
    }

    @Override
    public void verify() throws WiringException {
        LOG.info("Verifying container.");
        try {
            for (Key<?> key : strategies.keySet()) {
                get(key);
            }
        } catch (Exception e) {
            throw new WiringException("An exception occurred while verifying the container", e);
        }
        for (Container child : children) {
            child.verify();
        }
        for (Container cascader : cascadingContainers) {
            cascader.verify();
        }
        LOG.info("Container verified.");
    }

    @Override
    public <M extends Module> Container loadModule(Class<M> moduleClass) {
        Module module = strategyFactory.create(moduleClass, Qualifier.NONE, Scopes.PROTOTYPE).get(this);
        for (Mapping<?> mapping : module.getMappings()) {
            Class<?> implementationType = mapping.getImplementationType();
            Object qualifier = mapping.getQualifier();
            if (qualifier.equals(Qualifier.NONE)) {
                qualifier = metadataAdapter.getQualifier(implementationType, implementationType.getAnnotations());
            }
            if (mapping instanceof InstanceMapping) {
                InstanceMapping<?> instanceMapping = (InstanceMapping) mapping;
                Object instance = instanceMapping.getInstance();
                for (Class<?> target : mapping.getTargetClasses()) {
                    addMapping(Locksmith.makeKey(target, qualifier), instance);
                }
                for (Key<?> targetGeneric : mapping.getTargetKeys()) {
                    addMapping(targetGeneric, instance);
                }
            } else {
                for (Class<?> target : mapping.getTargetClasses()) {
                    addMapping(Locksmith.makeKey(target, qualifier), implementationType, mapping.getScope());
                }
                for (Key<?> targetGeneric : mapping.getTargetKeys()) {
                    addMapping(targetGeneric, implementationType, mapping.getScope());
                }
            }
        }
        for (Map.Entry<Key, Class> entry : module.getNonManagedComponentFactories().entrySet()) {
            registerNonManagedComponentFactory(entry.getKey(), entry.getValue());
        }
        return this;
    }

    @Override
    public synchronized Container addCascadingContainer(Container container) {
        if (this == container.getReal()) {
            throw new CircularReferenceException("Cannot add a container as a cascading container of itself");
        }
        if (isThisCascaderOfTarget(container)) {
            throw new CircularReferenceException("Cannot add a container as a cascader of one of its cascaders");
        }
        if (isTargetChildOfThis(container)) {
            throw new CircularReferenceException("Cannot add a child container as a cascader");
        }
        if (thisHasChildrenInCommonWithTarget(container)) {
            throw new CircularReferenceException("Cannot add a container as a cascader if the containers have children in common");
        }
        cascadingContainers.add(container);
        for (Container child : children) {
            child.addCascadingContainer(container);
        }
        return this;
    }

    @Override
    public synchronized Container addChild(Container child) {
        if (this == child.getReal()) {
            throw new CircularReferenceException("Cannot add a container as a child of itself");
        }
        if (isTargetCascaderOfThis(child)) {
            throw new CircularReferenceException("Cannot add a container as a child if it is already a cascader");
        }
        if (isThisCascaderOfTarget(child)) {
            throw new CircularReferenceException("Cannot add a container as a child of the one of the container's cascaders");
        }
        if (isThisChildOfTarget(child)) {
            throw new CircularReferenceException("Cannot add a container as a child of one of it's children");
        }
        children.add(child);
        for (Container cascadingContainer : cascadingContainers) {
            child.addCascadingContainer(cascadingContainer);
        }
        return this;
    }

    @Override
    public Container newEmptyClone() {
        return strategyFactory.create(this.getClass(), Qualifier.NONE, Scopes.SINGLETON).get(this);
    }

    @Override
    public Container addListener(Class<? extends ComponentInitializationListener> listenerClass) {
        ComponentStrategy strategy = strategyFactory.create(listenerClass, Qualifier.NONE, Scopes.SINGLETON);
        getInitializationListeners().add((ComponentInitializationListener) strategy.get(this));
        return this;
    }

    @Override
    public <T> Container add(Class<T> componentType, Class<? extends T> implementationType) {
        add(Locksmith.makeKey(componentType, metadataAdapter.getQualifier(implementationType, implementationType.getAnnotations())), implementationType);
        return this;
    }

    @Override
    public <T> Container add(Class<T> componentType, Object qualifier, Class<? extends T> implementationType) {
        add(Locksmith.makeKey(componentType, qualifier), implementationType);
        return this;
    }

    @Override
    public <T> Container add(Key<T> key, Class<? extends T> implementationType) {
        addMapping(key, implementationType, Scopes.SINGLETON);
        return this;
    }

    @Override
    public <T, I extends T> Container addInstance(Class<T> componentType, I implementation) {
        addInstance(Locksmith.makeKey(componentType, metadataAdapter.getQualifier(implementation.getClass(), implementation.getClass().getAnnotations())), implementation);
        return this;
    }

    @Override
    public <T, I extends T> Container addInstance(Class<T> componentType, Object qualifier, I implementation) {
        addInstance(Locksmith.makeKey(componentType, qualifier), implementation);
        return this;
    }

    @Override
    public <T, I extends T> Container addInstance(Key<T> key, I implementation) {
        addMapping(key, implementation);
        return this;
    }

    @Override
    public Container addCustomProvider(Class<?> providedType, Class<?> customProviderType) {
        addCustomProvider(Locksmith.makeKey(providedType, metadataAdapter.getQualifier(providedType, providedType.getAnnotations())), customProviderType);
        return this;
    }

    @Override
    public Container addCustomProvider(Key<?> providedTypeKey, Class<?> customProviderType) {
        Object qualifier = providedTypeKey.getQualifier();
        Key<?> providerKey = Locksmith.makeKey(customProviderType, qualifier);
        ComponentStrategy providerStrategy = getStrategy(providerKey);
        if (providerStrategy == null) {
            providerStrategy = strategyFactory.create(customProviderType, qualifier, Scopes.SINGLETON);
        }
        putStrategy(providerKey, providerStrategy);
        putStrategy(providedTypeKey, strategyFactory.createCustomStrategy(providerStrategy, qualifier));
        return this;
    }

    @Override
    public Container addCustomProvider(Class<?> providedType, Object customProvider) {
        addCustomProvider(Locksmith.makeKey(providedType, metadataAdapter.getQualifier(providedType, providedType.getAnnotations())), customProvider);
        return this;
    }

    @Override
    public Container addCustomProvider(Key<?> providedTypeKey, Object customProvider) {
        Object qualifier = providedTypeKey.getQualifier();
        Key<?> providerKey = Locksmith.makeKey(customProvider.getClass(), qualifier);
        ComponentStrategy providerStrategy = getStrategy(providerKey);
        if (providerStrategy == null) {
            providerStrategy = strategyFactory.createInstanceStrategy(customProvider, qualifier);
        }
        putStrategy(providerKey, providerStrategy);
        putStrategy(providedTypeKey, strategyFactory.createCustomStrategy(providerStrategy, qualifier));
        return this;
    }

    @Override
    public Container registerNonManagedComponentFactory(Key<?> factoryKey, Class producedType) {
        addMapping(factoryKey, dynamicComponentFactory.createNonManagedComponentFactory(factoryKey.getTargetClass(), producedType, this));
        return this;
    }

    @Override
    public synchronized Container registerCompositeTarget(Class<?> targetInterface) {
        registerCompositeTarget(Locksmith.makeKey(targetInterface));
        return this;
    }

    @Override
    public Container registerCompositeTarget(Class<?> targetInterface, Object qualifier) {
        registerCompositeTarget(Locksmith.makeKey(targetInterface, qualifier));
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public synchronized Container registerCompositeTarget(Key targetKey) {
        Object composite = dynamicComponentFactory.createCompositeHandler(targetKey.getTargetClass(), this);
        ComponentStrategy compositeStrategy = new TopLevelStrategy(strategyFactory.createInstanceStrategy(composite, targetKey.getQualifier()));
        putStrategy(targetKey, compositeStrategy);
        return this;
    }

    @Override
    public Container registerDeconstructedApi(Class<?> targetInterface) {
        registerDeconstructedApi(Locksmith.makeKey(targetInterface));
        return this;
    }

    @Override
    public Container registerDeconstructedApi(Class<?> targetInterface, Object qualifier) {
        registerDeconstructedApi(Locksmith.makeKey(targetInterface, qualifier));
        return this;
    }

    @Override
    public Container registerDeconstructedApi(Key<?> targetKey) {
        Object delegatingService = dynamicComponentFactory.createDelegatingService(targetKey.getTargetClass(), this);
        ComponentStrategy strategy = strategyFactory.createInstanceStrategy(delegatingService, targetKey.getQualifier());
        putStrategy(targetKey, strategy);
        return this;
    }

    @Override
    public List<ComponentInitializationListener> getInitializationListeners() {
        return componentInitializationListeners;
    }

    @Override
    public List<Object> getAllComponents() {
        List<Object> components = new LinkedList<Object>();
        components.addAll(getInitializationListeners());
        for (SortedSet<ComponentStrategy<?>> strategySet : strategies.values()) {
            for (ComponentStrategy<?> strategy : strategySet) {
                components.add(strategy.get(this));
            }
        }
        for (Container child : children) {
            components.addAll(child.getAllComponents());
        }
        return components;
    }

    @Override
    public List<Container> getCascadingContainers() {
        List<Container> result = new LinkedList<Container>(cascadingContainers);
        for (Container child : getChildren()) {
            result.addAll(child.getCascadingContainers());
        }
        for (Container cascader : cascadingContainers) {
            result.addAll(cascader.getChildren());
        }
        return result;
    }

    @Override
    public List<Container> getChildren() {
        List<Container> result = new LinkedList<Container>(children);
        for (Container child : children) {
            result.addAll(child.getChildren());
        }
        return result;
    }

    @Override
    public Container getReal() {
        return this;
    }

    @Override
    public Container makeInjectable() {
        addInstance(Container.class, this);
        addInstance(Provider.class, this);
        return this;
    }

    @Override
    public <T> T get(Class<T> clazz, SelectionAdvisor ... advisors) {
        return get(Locksmith.makeKey(clazz), advisors);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(Class<T> clazz, Object qualifier, SelectionAdvisor ... advisors) {
        return get(Locksmith.makeKey(clazz, qualifier), advisors);
    }

    @Override
    public <T> T get(final Key<T> key, SelectionAdvisor ... advisors) {

        @SuppressWarnings("unchecked")
        ComponentStrategy<T> strategy = getStrategy(key, advisors);

        if (strategy == null) {

            if (metadataAdapter.getProviderClass().isAssignableFrom(key.getTargetClass())) {

                //TODO this is slow, refactor to cache the type in the key and to reuse the strategy
                Key targetKey = Locksmith.makeKey(new ParameterRef() {
                    @Override
                    public Type getType() {
                        return ((ParameterizedType) key.getType()).getActualTypeArguments()[0];
                    }
                }, key.getQualifier());

                T instance = dynamicComponentFactory.createManagedComponentFactory(metadataAdapter.getProviderClass(), targetKey, this);
                strategy = strategyFactory.createInstanceStrategy(instance, Qualifier.NONE);
                putStrategy(key, strategy);

            } else {
                throw new MissingDependencyException("No components of type [" + key.getType() + "] with qualifier [" + key.getQualifier() + "] have been registered with the container");
            }

        }

        return strategy.get(this);

    }

    @Override
    public <T> List<T> getAll(Class<T> clazz, SelectionAdvisor... advisors) {
        return getAll(Locksmith.makeKey(clazz), advisors);
    }

    @Override
    public <T> List<T> getAll(Class<T> clazz, Object qualifier, SelectionAdvisor... advisors) {
        return getAll(Locksmith.makeKey(clazz, qualifier), advisors);
    }

    @Override
    public <T> List<T> getAll(Key<T> key, SelectionAdvisor... advisors) {
        List<T> components = new LinkedList<T>();
        List<ComponentStrategy<T>> componentStrategies = getAllStrategies(key, advisors);
        for (ComponentStrategy<T> strategy : componentStrategies) {
            components.add(strategy.get(this));
        }
        return components;
    }

    protected synchronized void addMapping(Key key, final Class<?> implementationType, Scope scope) {

        Key componentKey = Locksmith.makeKey(implementationType, key.getQualifier());

        ComponentStrategy strategy = getStrategy(componentKey, new SelectionAdvisor() {
            @Override
            public boolean validSelection(ComponentStrategy<?> candidateStrategy) {
                return candidateStrategy.getComponentType() == implementationType;
            }
        });

        if (strategy == null) {
            strategy = strategyFactory.create(implementationType, key.getQualifier(), scope);
            putStrategy(componentKey, strategy);
        }

        putStrategy(key, strategy);

    }

    protected synchronized void addMapping(Key key, Object implementation) {

        ComponentStrategy newStrategy = strategyFactory.createInstanceStrategy(implementation, key.getQualifier());
        putStrategy(key, newStrategy);
        putStrategy(Locksmith.makeKey(implementation.getClass()), newStrategy);

    }

     @Override
     public <T> ComponentStrategy<T> getStrategy(Key<T> key, SelectionAdvisor ... advisors) {

        Object qualifier = key.getQualifier();
        boolean qualified = !Qualifier.NONE.equals(qualifier);

        SortedSet<ComponentStrategy<T>> strategySet = getStrategySet(key);
        if (strategySet != null) {
            for (ComponentStrategy<T> strategy : strategySet) {
                boolean valid = true;
                if (qualified && !qualifier.equals(strategy.getQualifier())) {
                    continue;
                }
                for (SelectionAdvisor advisor : advisors) {
                    if (!advisor.validSelection(strategy)) {
                        valid = false;
                        break;
                    }
                }
                if (valid) {
                    return strategy;
                }
            }
        }
        for (Container child : children) {
            ComponentStrategy<T> strategy = child.getStrategy(key);
            if (strategy != null) {
                return strategy;
            }
        }
        for (Container container : cascadingContainers) {
            ComponentStrategy<T> strategy = container.getStrategy(key);
            if (strategy != null) {
                return strategy;
            }
        }
        return null;
    }

    @Override
    public <T> List<ComponentStrategy<T>> getAllStrategies(final Key<T> key, SelectionAdvisor... advisors) {

        List<ComponentStrategy<T>> allStrategies = new LinkedList<ComponentStrategy<T>>();

        Object qualifier = key.getQualifier();
        boolean qualified = !Qualifier.NONE.equals(qualifier);

        SortedSet<ComponentStrategy<T>> strategySet = getStrategySet(key);
        if (strategySet != null) {
            for (ComponentStrategy<T> strategy : strategySet) {
                for (SelectionAdvisor advisor : advisors) {
                    if (qualified && !qualifier.equals(strategy.getQualifier())) {
                        continue;
                    }
                    if (advisor.validSelection(strategy)) {
                        allStrategies.add(strategy);
                    }
                }
            }
        }
        for (Container child : children) {
            List<ComponentStrategy<T>> childAllStrategies = child.getAllStrategies(key, advisors);
            allStrategies.addAll(childAllStrategies);
        }
        for (Container container : cascadingContainers) {
            List<ComponentStrategy<T>> containerAllStrategies = container.getAllStrategies(key, advisors);
            allStrategies.addAll(containerAllStrategies);
        }
        return allStrategies;
    }

    @SuppressWarnings("unchecked")
    protected <T> SortedSet<ComponentStrategy<T>> getStrategySet(Key<T> key) {
        SortedSet<ComponentStrategy<?>> strategySet = strategies.get(key);
        return (SortedSet<ComponentStrategy<T>>) (SortedSet) strategySet;
    }

    protected void putStrategy(Key key, ComponentStrategy<?> strategy) {
        SortedSet<ComponentStrategy<?>> strategySet = strategies.get(key);
        if (strategySet == null) {
            strategySet = new TreeSet<ComponentStrategy<?>>(new StrategyComparator() {
                @Override
                public int compare(ComponentStrategy<?> strategy, ComponentStrategy<?> strategy2) {
                    if (strategy.equals(strategy2) ||
                            //TODO need a better way to ensure only one composite/delegating service etc is allowed
                            (strategy.getComponentType().equals(strategy2.getComponentType())
                                    && strategy.getQualifier().equals(strategy2.getQualifier()
                            ) && !Proxy.isProxyClass(strategy.getComponentType()))) {
                        return 0;
                    } else if (strategy instanceof TopLevelStrategy) {
                        return -1;
                    } else if (strategy2 instanceof TopLevelStrategy) {
                        return 1;
                    } else if (strategy.isDecorator()) {
                        return -1;
                    } else if (strategy2.isDecorator()) {
                        return 1;
                    }
                    return -1;
                }
            });
            strategies.put(key, strategySet);
        }
        strategySet.add(strategy);
    }

    protected boolean isTargetCascaderOfThis(Container target) {
        for (Container cascader : getCascadingContainers()) {
            if (cascader.getReal() == target.getReal()) {
                return true;
            }
        }
        return false;
    }

    protected boolean isTargetChildOfThis(Container target) {
        for (Container child : getChildren()) {
            if (child.getReal() == target.getReal()) {
                return true;
            }
        }
        return false;
    }

    protected boolean isThisCascaderOfTarget(Container target) {
        for (Container cascader : target.getCascadingContainers()) {
            if (cascader.getReal() == this) {
                return true;
            }
        }
        return false;
    }

    protected boolean isThisChildOfTarget(Container target) {
        for (Container child : target.getChildren()) {
            if (child.getReal() == this) {
                return true;
            }
        }
        return false;
    }

    protected boolean thisHasChildrenInCommonWithTarget(Container target) {
        for (Container targetChild : target.getChildren()) {
            for (Container child : getChildren()) {
                if (targetChild.getReal() == child.getReal()) {
                    return true;
                }
            }
        }
        return false;
    }

}
