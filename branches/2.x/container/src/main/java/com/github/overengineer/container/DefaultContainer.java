package com.github.overengineer.container;

import com.github.overengineer.container.dynamic.DynamicComponentFactory;
import com.github.overengineer.container.key.*;
import com.github.overengineer.container.module.InstanceMapping;
import com.github.overengineer.container.module.Mapping;
import com.github.overengineer.container.module.Module;
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
    private final KeyRepository keyRepository;
    private final DynamicComponentFactory dynamicComponentFactory;
    private final List<ComponentInitializationListener> componentInitializationListeners;

    public DefaultContainer(ComponentStrategyFactory strategyFactory, KeyRepository keyRepository, DynamicComponentFactory dynamicComponentFactory, List<ComponentInitializationListener> componentInitializationListeners) {
        this.strategyFactory = strategyFactory;
        this.keyRepository = keyRepository;
        this.dynamicComponentFactory = dynamicComponentFactory;
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
    public Container loadModule(Class<? extends Module> moduleClass) {
        Module module = strategyFactory.create(moduleClass).get(this);
        for (Mapping<?> mapping : module.getMappings()) {
            Class<?> implementationType = mapping.getImplementationType();
            String name = mapping.getName();
            if (mapping instanceof InstanceMapping) {
                InstanceMapping<?> instanceMapping = (InstanceMapping) mapping;
                Object instance = instanceMapping.getInstance();
                for (Class<?> target : mapping.getTargetClasses()) {
                    addMapping(keyRepository.retrieveKey(target, name), instance);
                }
                for (Key<?> targetGeneric : mapping.getTargetKeys()) {
                    addMapping(targetGeneric, instance);
                }
            } else {
                for (Class<?> target : mapping.getTargetClasses()) {
                    addMapping(keyRepository.retrieveKey(target, name), implementationType);
                }
                for (Key<?> targetGeneric : mapping.getTargetKeys()) {
                    addMapping(targetGeneric, implementationType);
                }
            }
        }
        for (Key factoryKey : module.getManagedComponentFactories()) {
            registerManagedComponentFactory(factoryKey);
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
        return strategyFactory.create(this.getClass()).get(this);
    }

    @Override
    public Container addListener(Class<? extends ComponentInitializationListener> listenerClass) {
        ComponentStrategy strategy = strategyFactory.create(listenerClass);
        getInitializationListeners().add((ComponentInitializationListener) strategy.get(this));
        return this;
    }

    @Override
    public <T> Container add(Class<T> componentType, Class<? extends T> implementationType) {
        add(keyRepository.retrieveKey(componentType), implementationType);
        return this;
    }

    @Override
    public <T> Container add(Class<T> componentType, String name, Class<? extends T> implementationType) {
        add(keyRepository.retrieveKey(componentType, name), implementationType);
        return this;
    }

    @Override
    public <T> Container add(Key<T> key, Class<? extends T> implementationType) {
        addMapping(key, implementationType);
        return this;
    }

    @Override
    public <T, I extends T> Container addInstance(Class<T> componentType, I implementation) {
        addInstance(keyRepository.retrieveKey(componentType), implementation);
        return this;
    }

    @Override
    public <T, I extends T> Container addInstance(Class<T> componentType, String name, I implementation) {
        addInstance(keyRepository.retrieveKey(componentType, name), implementation);
        return this;
    }

    @Override
    public <T, I extends T> Container addInstance(Key<T> key, I implementation) {
        addMapping(key, implementation);
        return this;
    }

    @Override
    public Container addCustomProvider(Class<?> providedType, Class<?> customProviderType) {
        addCustomProvider(keyRepository.retrieveKey(providedType), customProviderType);
        return this;
    }

    @Override
    public Container addCustomProvider(Key<?> providedTypeKey, Class<?> customProviderType) {
        Key<?> providerKey = keyRepository.retrieveKey(customProviderType);
        ComponentStrategy providerStrategy = getStrategy(providerKey);
        if (providerStrategy == null) {
            providerStrategy = strategyFactory.create(customProviderType);
        }
        keyRepository.addKey(providerKey);
        keyRepository.addKey(providedTypeKey);
        putStrategy(providerKey, providerStrategy);
        putStrategy(providedTypeKey, strategyFactory.createCustomStrategy(providerStrategy));
        return this;
    }

    @Override
    public Container addCustomProvider(Class<?> providedType, Object customProvider) {
        addCustomProvider(keyRepository.retrieveKey(providedType), customProvider);
        return this;
    }

    @Override
    public Container addCustomProvider(Key<?> providedTypeKey, Object customProvider) {
        Key<?> providerKey = keyRepository.retrieveKey(customProvider.getClass());
        ComponentStrategy providerStrategy = getStrategy(providerKey);
        if (providerStrategy == null) {
            providerStrategy = strategyFactory.createInstanceStrategy(customProvider);
        }
        keyRepository.addKey(providerKey);
        keyRepository.addKey(providedTypeKey);
        putStrategy(providerKey, providerStrategy);
        putStrategy(providedTypeKey, strategyFactory.createCustomStrategy(providerStrategy));
        return this;
    }

    @Override
    public Container registerManagedComponentFactory(Key<?> factoryKey) {
        //TODO perform checks and throw informative exceptions
        Type producedType = ((ParameterizedType) factoryKey.getType()).getActualTypeArguments()[0];
        Key targetKey = keyRepository.retrieveKey(producedType);
        addMapping(factoryKey, dynamicComponentFactory.createManagedComponentFactory(factoryKey.getTargetClass(), targetKey, this));
        return this;
    }

    @Override
    public Container registerNonManagedComponentFactory(Key<?> factoryKey, Class producedType) {
        addMapping(factoryKey, dynamicComponentFactory.createNonManagedComponentFactory(factoryKey.getTargetClass(), producedType, this));
        return this;
    }

    @Override
    public synchronized Container registerCompositeTarget(Class<?> targetInterface) {
        registerCompositeTarget(keyRepository.retrieveKey(targetInterface));
        return this;
    }

    @Override
    public Container registerCompositeTarget(Class<?> targetInterface, String name) {
        registerCompositeTarget(keyRepository.retrieveKey(targetInterface, name));
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public synchronized Container registerCompositeTarget(Key targetKey) {
        Object composite = dynamicComponentFactory.createCompositeHandler(targetKey.getTargetClass(), this);
        ComponentStrategy compositeStrategy = new TopLevelStrategy(strategyFactory.createInstanceStrategy(composite));
        putStrategy(targetKey, compositeStrategy);
        return this;
    }

    @Override
    public Container registerDelegatingService(Class<?> targetInterface) {
        registerDelegatingService(keyRepository.retrieveKey(targetInterface));
        return this;
    }

    @Override
    public Container registerDelegatingService(Class<?> targetInterface, String name) {
        registerDelegatingService(keyRepository.retrieveKey(targetInterface, name));
        return this;
    }

    @Override
    public Container registerDelegatingService(Key<?> targetKey) {
        Object delegatingService = dynamicComponentFactory.createDelegatingService(targetKey.getTargetClass(), this);
        ComponentStrategy strategy = strategyFactory.createInstanceStrategy(delegatingService);
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
        return get(keyRepository.retrieveKey(clazz), advisors);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(Class<T> clazz, String name, SelectionAdvisor ... advisors) {
        return get(keyRepository.retrieveKey(clazz, name), advisors);
    }

    @Override
    public <T> T get(Key<T> key, SelectionAdvisor ... advisors) {

        @SuppressWarnings("unchecked")
        ComponentStrategy<T> strategy = getStrategy(key, advisors);

        if (strategy == null) {
            String name = key.getName();
            if (name != null) {
                throw new MissingDependencyException("No components of type [" + key.getType() + "] with name [" + name + "] have been registered with the container");
            }
            throw new MissingDependencyException("No components of type [" + key.getType() + "] have been registered with the container");
        }

        return strategy.get(this);

    }

    @Override
    public <T> List<T> getAll(Class<T> clazz, SelectionAdvisor... advisors) {
        return getAll(keyRepository.retrieveKey(clazz), advisors);
    }

    @Override
    public <T> List<T> getAll(Class<T> clazz, String name, SelectionAdvisor... advisors) {
        return getAll(keyRepository.retrieveKey(clazz, name), advisors);
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

    protected synchronized void addMapping(Key key, Class<?> implementationType) {

        ComponentStrategy newStrategy = strategyFactory.create(implementationType);
        keyRepository.addKey(key);
        putStrategy(key, newStrategy);
        putStrategy(keyRepository.retrieveKey(implementationType, key.getName()), newStrategy);

    }

    protected synchronized void addMapping(Key key, Object implementation) {

        ComponentStrategy newStrategy = strategyFactory.createInstanceStrategy(implementation);
        keyRepository.addKey(key);
        putStrategy(key, newStrategy);
        putStrategy(keyRepository.retrieveKey(implementation.getClass()), newStrategy);

    }

    @Override
     public <T> ComponentStrategy<T> getStrategy(Key<T> key, SelectionAdvisor ... advisors) {

        SortedSet<ComponentStrategy<T>> strategySet = getStrategySet(key);
        if (strategySet != null) {
            for (ComponentStrategy<T> strategy : strategySet) {
                boolean valid = true;
                for (SelectionAdvisor advisor : advisors) {
                    if (!advisor.validSelection(strategy.getComponentType())) {
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
    public <T> List<ComponentStrategy<T>> getAllStrategies(Key<T> key, SelectionAdvisor... advisors) {

        List<ComponentStrategy<T>> allStrategies = new LinkedList<ComponentStrategy<T>>();

        SortedSet<ComponentStrategy<T>> strategySet = getStrategySet(key);
        if (strategySet != null) {
            for (ComponentStrategy<T> strategy : strategySet) {
                for (SelectionAdvisor advisor : advisors) {
                    if (advisor.validSelection(strategy.getComponentType())) {
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
                            (strategy.getComponentType().equals(strategy2.getComponentType()) &&
                                    !Proxy.isProxyClass(strategy.getComponentType()))) {
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
