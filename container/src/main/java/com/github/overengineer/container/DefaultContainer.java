package com.github.overengineer.container;

import com.github.overengineer.container.factory.MetaFactory;
import com.github.overengineer.container.key.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

/**
 *
 * TODO Features:
 * TODO scoped containers and proxies, factories, enhance interceptor rules (@OR, @AND, @NOT)
 * TODO consider a sort of decorator placeholder strategy to handle decorator being added before delegate that throws decoration exception if invoked
 * TODO then combine this with a check on existing strategies for this type and handling it appropriately
 * TODO messaging/eventing
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
 * TODO add a getProducedType method to the ComponentStrategy interface, leverage to move to single map lookup - look into
 * TODO accomplishing by also adding to the Instantiator interface, then we can get via either instantiator.get, delegate.get, or instance.get
 *
 *
 * @author rees.byars
 */
public class DefaultContainer implements Container {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultContainer.class);

    protected final Map<SerializableKey, ComponentStrategy<?>> strategies = new HashMap<SerializableKey, ComponentStrategy<?>>();
    protected final Map<String, Object> properties = new HashMap<String, Object>();
    protected final List<Container> cascadingContainers = new ArrayList<Container>();
    protected final List<Container> children = new ArrayList<Container>();
    protected final ComponentStrategyFactory strategyFactory;
    protected final KeyRepository keyRepository;
    protected final MetaFactory metaFactory;
    private final List<ComponentInitializationListener> componentInitializationListeners;

    public DefaultContainer(ComponentStrategyFactory strategyFactory, KeyRepository keyRepository, MetaFactory metaFactory, List<ComponentInitializationListener> componentInitializationListeners) {
        this.strategyFactory = strategyFactory;
        this.keyRepository = keyRepository;
        this.metaFactory = metaFactory;
        this.componentInitializationListeners = componentInitializationListeners;
    }

    @Override
    public void verify() throws WiringException {
        LOG.info("Verifying container.");
        try {
            for (SerializableKey key : strategies.keySet()) {
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
        for (Map.Entry<Class<?>, List<Class<?>>> componentEntry : module.getTypeMappings().entrySet()) {
            Class<?> implementationType = componentEntry.getKey();
            for (Class<?> targetInterface : componentEntry.getValue()) {
                add(keyRepository.retrieveKey(targetInterface), implementationType);
            }
        }
        for (Map.Entry<Class<?>, Object> componentEntry : module.getInstanceMappings().entrySet()) {
            addInstance(keyRepository.retrieveKey(componentEntry.getKey()), componentEntry.getValue());
        }
        for (Map.Entry<Class<?>, List<SerializableKey>> componentEntry : module.getGenericTypeMappings().entrySet()) {
            Class<?> implementationType = componentEntry.getKey();
            for (SerializableKey targetGeneric : componentEntry.getValue()) {
                add(targetGeneric, implementationType);
            }
        }
        for (Map.Entry<SerializableKey, Object> componentEntry : module.getGenericInstanceMappings().entrySet()) {
            addInstance(componentEntry.getKey(), componentEntry.getValue());
        }
        for (Map.Entry<String, Object> propertyEntry : module.getProperties().entrySet()) {
            addProperty(propertyEntry.getKey(), propertyEntry.getValue());
        }
        for (SerializableKey factoryKey : module.getManagedComponentFactories()) {
            registerManagedComponentFactory(factoryKey);
        }
        for (Map.Entry<SerializableKey, Class> entry : module.getNonManagedComponentFactories().entrySet()) {
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
    public <T> Container add(SerializableKey key, Class<? extends T> implementationType) {
        addMapping(key, implementationType);
        return this;
    }

    @Override
    public <T, I extends T> Container addInstance(Class<T> componentType, I implementation) {
        addInstance(keyRepository.retrieveKey(componentType), implementation);
        return this;
    }

    @Override
    public <T, I extends T> Container addInstance(SerializableKey key, I implementation) {
        addMapping(key, implementation);
        return this;
    }

    @Override
    public Container registerManagedComponentFactory(SerializableKey factoryKey) {
        //TODO perform checks and throw informative exceptions
        Type producedType = ((ParameterizedType) factoryKey.getType()).getActualTypeArguments()[0];
        SerializableKey targetKey = keyRepository.retrieveKey(producedType);
        addInstance(factoryKey, metaFactory.createManagedComponentFactory(factoryKey.getTargetClass(), targetKey, this));
        return this;
    }

    @Override
    public Container registerNonManagedComponentFactory(SerializableKey factoryKey, Class producedType) {
        addInstance(factoryKey, metaFactory.createNonManagedComponentFactory(factoryKey.getTargetClass(), producedType, this));
        return this;
    }

    @Override
    public Container addProperty(String propertyName, Object propertyValue) {
        properties.put(propertyName, propertyValue);
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
        for (ComponentStrategy strategy : strategies.values()) {
            components.add(strategy.get(this));
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
        addInstance(ComponentProvider.class, this);
        addInstance(PropertyProvider.class, this);
        return this;
    }

    @Override
    public <T> T get(Class<T> clazz) {
        return get(keyRepository.retrieveKey(clazz));
    }

    @Override
    public <T> T get(Type type) {
        return get(keyRepository.retrieveKey(type));
    }

    @Override
    public <T> T get(SerializableKey key) {

        @SuppressWarnings("unchecked")
        ComponentStrategy<T> strategy = (ComponentStrategy<T>) strategies.get(key);

        if (strategy == null) {
            for (Container child : children) {
                try {
                    return child.get(key);
                } catch (MissingDependencyException e) {
                    //ignore
                }
            }
            for (Container container : cascadingContainers) {
                try {
                    return container.get(key);
                } catch (MissingDependencyException e) {
                    //ignore
                }
            }
            throw new MissingDependencyException("No components of type [" + key.getType() + "] have been registered with the container");
        }

        return strategy.get(this);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getProperty(Class<T> clazz, String name) {
        Object property = properties.get(name);
        if (property == null) {
            for (Container child : children) {
                try {
                    return child.getProperty(clazz, name);
                } catch (MissingDependencyException e) {
                    //ignore
                }
            }
            for (Container container : cascadingContainers) {
                try {
                    return container.getProperty(clazz, name);
                } catch (MissingDependencyException e) {
                    //ignore
                }
            }
            throw new MissingDependencyException("No property of name [" + name + "] has been registered with the container");
        }
        return (T) property;
    }

    protected synchronized void addMapping(SerializableKey key, Class<?> implementationType) {

        if (!key.getTargetClass().isInterface()) {
            throw new BadDesignException("We force you to map dependencies only to interfaces. The type [" + key.getTargetClass().getName() + "] is not an interface.  Don't like it?  I don't give a shit.");
        }

        ComponentStrategy existing = strategies.get(key);
        ComponentStrategy newStrategy;

        if (existing != null) {
            newStrategy = strategyFactory.createDecoratorStrategy(implementationType, existing);
            strategies.remove(keyRepository.retrieveKey(existing.getProvidedType()));
        } else {
            keyRepository.addKey(key);
            newStrategy = strategyFactory.create(implementationType);
        }

        strategies.put(key, newStrategy);
        strategies.put(keyRepository.retrieveKey(implementationType), newStrategy);

    }

    protected synchronized void addMapping(SerializableKey key, Object implementation) {

        if (!key.getTargetClass().isInterface()) {
            throw new BadDesignException("We force you to map dependencies only to interfaces. The type [" + key.getTargetClass().getName() + "] is not an interface.  Don't like it?  I don't give a shit.");
        }

        ComponentStrategy existing = strategies.get(key);

        if (existing != null) {
            strategies.remove(keyRepository.retrieveKey(existing.getProvidedType()));
        }

        keyRepository.addKey(key);
        ComponentStrategy strategy = strategyFactory.createInstanceStrategy(implementation);
        strategies.put(keyRepository.retrieveKey(implementation.getClass()), strategy);
        strategies.put(key, strategy);
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
