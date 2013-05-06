package com.github.overengineer.scope.container;

import com.github.overengineer.scope.container.type.Key;
import com.github.overengineer.scope.container.type.KeyGenerator;
import com.github.overengineer.scope.container.type.SerializableKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
 * TODO child container impl that uses same strategy factory as parent and can remove itself from parent? makeBabay()
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
 * TODO cleanup interceptor impl, move from extensions to decorations
 * TODO throw decorationexception if a defaultinstantiator tries to reference
 * TODO move aspect list to invocation factory, create new "aspect cache" interface for factory to implement,
 * TODO then the aop container can just interface with its cache and that of its children
 *
 *
 * TODO - Naming schemes
 * TODO cascading container = heirloom (it's passed down from parent to child to child to child, etc.)
 * TODO addChild = adopt - nest
 * TODO makeChild = makeBaby
 *
 *
 * @author rees.byars
 */
public class DefaultContainer implements Container {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultContainer.class);

    protected final Map<SerializableKey, Class<?>> mappings = new HashMap<SerializableKey, Class<?>>();
    protected final Map<Class<?>, ComponentStrategy<?>> strategies = new HashMap<Class<?>, ComponentStrategy<?>>();
    protected final Map<String, Object> properties = new HashMap<String, Object>();
    protected final List<ComponentInitializationListener> initializationListeners = new ArrayList<ComponentInitializationListener>();
    protected final List<Container> cascadingContainers = new ArrayList<Container>();
    protected final List<Container> children = new ArrayList<Container>();
    protected final ComponentStrategyFactory strategyFactory;
    protected final KeyGenerator keyGenerator;

    public DefaultContainer(ComponentStrategyFactory strategyFactory, KeyGenerator keyGenerator) {
        this.strategyFactory = strategyFactory;
        this.keyGenerator = keyGenerator;
        addInstance(Container.class, this);
        addInstance(Provider.class, this);
        addInstance(ComponentProvider.class, this);
        addInstance(PropertyProvider.class, this);
    }

    @Override
    public void verify() throws WiringException {
        LOG.info("Verifying container.");
        try {
            for (Key componentType : mappings.keySet()) {
                get(componentType);
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
        Module module = strategyFactory.create(moduleClass, initializationListeners).get(this);
        for (Map.Entry<Class<?>, List<Class<?>>> componentEntry : module.getTypeMappings().entrySet()) {
            for (Class<?> cls : componentEntry.getValue()) {
                addMapping(keyGenerator.fromClass(componentEntry.getKey()), cls);
            }
        }
        for (Map.Entry<Class<?>, Object> componentEntry : module.getInstanceMappings().entrySet()) {
            addMapping(keyGenerator.fromClass(componentEntry.getKey()), componentEntry.getValue());
        }
        for (Map.Entry<SerializableKey, List<Class<?>>> componentEntry : module.getGenericTypeMappings().entrySet()) {
            for (Class<?> cls : componentEntry.getValue()) {
                addMapping(componentEntry.getKey(), cls);
            }
        }
        for (Map.Entry<SerializableKey, Object> componentEntry : module.getGenericInstanceMappings().entrySet()) {
            addMapping(componentEntry.getKey(), componentEntry.getValue());
        }
        for (Map.Entry<String, Object> propertyEntry : module.getProperties().entrySet()) {
            addProperty(propertyEntry.getKey(), propertyEntry.getValue());
        }
        return get(Container.class);
    }

    @Override
    public Container addCascadingContainer(Container container) {
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
        return get(Container.class);
    }

    @Override
    public Container addChild(Container child) {
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
        return get(Container.class);
    }

    @Override
    public Container addListener(Class<? extends ComponentInitializationListener> listenerClass) {
        ComponentStrategy strategy = strategyFactory.create(listenerClass, Collections.<ComponentInitializationListener>emptyList());
        initializationListeners.add((ComponentInitializationListener) strategy.get(this));
        return get(Container.class);
    }

    @Override
    public <T> Container add(Class<T> componentType, Class<? extends T> implementationType) {
        add(keyGenerator.fromClass(componentType), implementationType);
        return get(Container.class);
    }

    @Override
    public <T> Container add(SerializableKey key, Class<? extends T> implementationType) {
        addMapping(key, implementationType);
        return get(Container.class);
    }

    @Override
    public <T, I extends T> Container addInstance(Class<T> componentType, I implementation) {
        addInstance(keyGenerator.fromClass(componentType), implementation);
        return get(Container.class);
    }

    @Override
    public <T, I extends T> Container addInstance(SerializableKey key, I implementation) {
        addMapping(key, implementation);
        return get(Container.class);
    }

    @Override
    public Container addProperty(String propertyName, Object propertyValue) {
        properties.put(propertyName, propertyValue);
        return get(Container.class);
    }

    @Override
    public List<Object> getAllComponents() {
        List<Object> components = new LinkedList<Object>();
        components.addAll(initializationListeners);
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
    public <T> T get(Class<T> clazz) {
        return get(keyGenerator.fromType(clazz));
    }

    @Override
    public <T> T get(Type type) {
        return get(keyGenerator.fromType(type));
    }

    @Override
    public <T> T get(Key key) {
        Class<?> implementationType = mappings.get(key);
        if (implementationType == null) {
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
        @SuppressWarnings("unchecked")
        ComponentStrategy<T> strategy = (ComponentStrategy<T>) strategies.get(implementationType);
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

    protected void addMapping(SerializableKey key, Class<?> implementationType) {

        if (!key.getTargetClass().isInterface()) {
            throw new BadDesignException("We force you to map dependencies only to interfaces. The type [" + key.getTargetClass().getName() + "] is not an interface.  Don't like it?  I don't give a shit.");
        }

        Class<?> existing = mappings.get(key);
        if (existing != null) {
            strategies.put(implementationType, strategyFactory.createDecoratorStrategy(implementationType, initializationListeners, existing, strategies.get(existing)));
        } else {
            strategies.put(implementationType, strategyFactory.create(implementationType, initializationListeners));
        }

        mappings.put(key, implementationType);
    }

    protected void addMapping(SerializableKey key, Object implementation) {

        if (!key.getTargetClass().isInterface()) {
            throw new BadDesignException("We force you to map dependencies only to interfaces. The type [" + key.getTargetClass().getName() + "] is not an interface.  Don't like it?  I don't give a shit.");
        }

        strategies.put(implementation.getClass(), strategyFactory.createInstanceStrategy(implementation, initializationListeners));
        mappings.put(key, implementation.getClass());
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
