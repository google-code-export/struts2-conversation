package com.github.overengineer.scope.struts2;

import com.github.overengineer.container.*;
import com.github.overengineer.container.key.Key;
import com.github.overengineer.container.key.KeyRepository;
import com.github.overengineer.container.module.Module;
import com.github.overengineer.scope.ActionProvider;
import com.github.overengineer.scope.CommonModule;
import com.github.overengineer.scope.conversation.ConversationModule;
import com.github.overengineer.scope.conversation.configuration.ConversationArbitrator;
import com.github.overengineer.scope.session.SessionModule;
import com.opensymphony.xwork2.inject.Inject;

import java.util.List;

public class StrutsModuleContainer implements Container {

    private static final long serialVersionUID = 3180479652636319036L;

    private com.opensymphony.xwork2.inject.Container container;
    private Container delegate = Clarence.please().makeYourStuffInjectable().gimmeThatTainer();
    private KeyRepository keyRepository = delegate.get(KeyRepository.class);

    @Inject
    public void setContainer(com.opensymphony.xwork2.inject.Container container) {
        this.container = container;
        init();
    }

    protected void init() {
        loadModule(CommonModule.class);
        loadModule(SessionModule.class);
        loadModule(ConversationModule.class);
        add(ConversationArbitrator.class, StrutsConversationArbitrator.class);
        addStrutsInstance(ActionProvider.class);
    }

    protected <T> void addStrutsInstance(Class<T> clazz) {
        addInstance(clazz, container.getInstance(clazz, container.getInstance(String.class, clazz.getName())));
    }

    @Override
    public void verify() throws WiringException {
        delegate.verify();
    }

    @Override
    public Container loadModule(Class<? extends Module> module) {
        return delegate.loadModule(module);
    }

    @Override
    public Container addCascadingContainer(Container container) {
        return delegate.addCascadingContainer(container);
    }

    @Override
    public Container addChild(Container container) {
        return delegate.addChild(container);
    }

    @Override
    public Container newEmptyClone() {
        throw new UnsupportedOperationException("cannot clone the struts2 container");
    }

    @Override
    public Container addListener(Class<? extends ComponentInitializationListener> listenerClass) {
        return delegate.addListener(listenerClass);
    }

    @Override
    public <T> Container add(Class<T> componentType, Class<? extends T> implementationType) {
        return delegate.add(componentType, implementationType);
    }

    @Override
    public <T> Container add(Class<T> componentType, Object qualifier, Class<? extends T> implementationType) {
        return delegate.add(componentType, qualifier, implementationType);
    }

    @Override
    public <T> Container add(Key<T> key, Class<? extends T> implementationType) {
        return delegate.add(key, implementationType);
    }

    @Override
    public <T, I extends T> Container addInstance(Class<T> componentType, I implementation) {
        return delegate.addInstance(componentType, implementation);
    }

    @Override
    public <T, I extends T> Container addInstance(Class<T> componentType, Object qualifier, I implementation) {
        return delegate.addInstance(componentType, qualifier, implementation);
    }

    @Override
    public <T, I extends T> Container addInstance(Key<T> key, I implementation) {
        return delegate.addInstance(key, implementation);
    }

    @Override
    public Container addCustomProvider(Class<?> providedType, Class<?> customProviderType) {
        return delegate.addCustomProvider(providedType, customProviderType);
    }

    @Override
    public Container addCustomProvider(Key<?> providedTypeKey, Class<?> customProviderType) {
        return delegate.addCustomProvider(providedTypeKey, customProviderType);
    }

    @Override
    public Container addCustomProvider(Class providedType, Object customProvider) {
        return delegate.addCustomProvider(providedType, customProvider);
    }

    @Override
    public Container addCustomProvider(Key providedTypeKey, Object customProvider) {
        return delegate.addCustomProvider(providedTypeKey, customProvider);
    }

    @Override
    public Container registerManagedComponentFactory(Key factoryKey) {
        return delegate.registerManagedComponentFactory(factoryKey);
    }

    @Override
    public Container registerNonManagedComponentFactory(Key factoryKey, Class producedType) {
        return delegate.registerNonManagedComponentFactory(factoryKey, producedType);
    }

    @Override
    public Container registerCompositeTarget(Class<?> targetInterface) {
        return delegate.registerCompositeTarget(targetInterface);
    }

    @Override
    public Container registerCompositeTarget(Class<?> targetInterface, Object qualifier) {
        return delegate.registerCompositeTarget(targetInterface, qualifier);
    }

    @Override
    public Container registerCompositeTarget(Key targetKey) {
        return delegate.registerCompositeTarget(targetKey);
    }

    @Override
    public Container registerDeconstructedApi(Class<?> targetInterface) {
        return delegate.registerDeconstructedApi(targetInterface);
    }

    @Override
    public Container registerDeconstructedApi(Class<?> targetInterface, Object qualifier) {
        return delegate.registerDeconstructedApi(targetInterface, qualifier);
    }

    @Override
    public Container registerDeconstructedApi(Key<?> targetKey) {
        return delegate.registerDeconstructedApi(targetKey);
    }

    @Override
    public List<ComponentInitializationListener> getInitializationListeners() {
        return delegate.getInitializationListeners();
    }

    @Override
    public List<Object> getAllComponents() {
        return delegate.getAllComponents();
    }

    @Override
    public List<Container> getCascadingContainers() {
        return delegate.getCascadingContainers();
    }

    @Override
    public List<Container> getChildren() {
        return delegate.getChildren();
    }

    @Override
    public Container getReal() {
        return delegate.getReal();
    }

    @Override
    public <T> ComponentStrategy<T> getStrategy(Key<T> key, SelectionAdvisor... advisors) {
        return delegate.getStrategy(key, advisors);
    }

    @Override
    public <T> List<ComponentStrategy<T>> getAllStrategies(Key<T> key, SelectionAdvisor... advisors) {
        return delegate.getAllStrategies(key, advisors);
    }

    @Override
    public Container makeInjectable() {
        return delegate.makeInjectable();
    }

    @Override
    public <T> T get(Class<T> clazz, SelectionAdvisor ... advisors) {
        return get(keyRepository.retrieveKey(clazz));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(Class<T> clazz, Object qualifier, SelectionAdvisor ... advisors) {
        return get(keyRepository.retrieveKey(clazz, qualifier));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(Key<T> key, SelectionAdvisor ... advisors) {
        if (key.getQualifier() instanceof String) {
            String string = container.getInstance(String.class, (String) key.getQualifier());
            if (string != null) {
                Class clazz = key.getTargetClass();
                if (clazz == long.class) {
                    return (T) Long.valueOf(string);
                } else if (clazz == int.class) {
                    return (T) Integer.valueOf(string);
                } else if (clazz == boolean.class) {
                    return (T) (Boolean) "true".equals(string);
                }
                return (T) string;
            }
        }

        return delegate.get(key);
    }

    @Override
    public <T> List<T> getAll(Class<T> clazz, SelectionAdvisor... advisors) {
        return delegate.getAll(clazz, advisors);
    }

    @Override
    public <T> List<T> getAll(Class<T> clazz, Object qualifier, SelectionAdvisor... advisors) {
        return delegate.getAll(clazz, qualifier, advisors);
    }

    @Override
    public <T> List<T> getAll(Key<T> key, SelectionAdvisor... advisors) {
        return delegate.getAll(key, advisors);
    }
}
