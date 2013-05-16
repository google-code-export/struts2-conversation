package com.github.overengineer.scope.struts2;

import com.github.overengineer.container.*;
import com.github.overengineer.container.key.SerializableKey;
import com.github.overengineer.scope.ActionProvider;
import com.github.overengineer.scope.CommonModule;
import com.github.overengineer.scope.conversation.ConversationModule;
import com.github.overengineer.scope.conversation.configuration.ConversationArbitrator;
import com.github.overengineer.scope.session.SessionModule;
import com.opensymphony.xwork2.inject.Inject;

import java.lang.reflect.Type;
import java.util.List;

public class StrutsModuleContainer implements Container {

    private static final long serialVersionUID = 3180479652636319036L;

    private com.opensymphony.xwork2.inject.Container container;
    private Container delegate = Clarence.please().gimmeThatTainer();

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

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getProperty(Class<T> clazz, String name) {
        String string = container.getInstance(String.class, name);
        if (clazz == long.class) {
            return (T) Long.valueOf(string);
        } else if (clazz == int.class) {
            return (T) Integer.valueOf(string);
        } else if (clazz == boolean.class) {
            return (T) (Boolean) "true".equals(string);
        }
        return (T) string;
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
    public <T> Container add(SerializableKey key, Class<? extends T> implementationType) {
        return delegate.add(key, implementationType);
    }

    @Override
    public <T, I extends T> Container addInstance(Class<T> componentType, I implementation) {
        return delegate.addInstance(componentType, implementation);
    }

    @Override
    public <T, I extends T> Container addInstance(SerializableKey key, I implementation) {
        return delegate.addInstance(key, implementation);
    }

    @Override
    public Container registerManagedComponentFactory(SerializableKey factoryKey) {
        return delegate.registerManagedComponentFactory(factoryKey);
    }

    @Override
    public Container registerNonManagedComponentFactory(SerializableKey factoryKey, Class producedType) {
        return delegate.registerNonManagedComponentFactory(factoryKey, producedType);
    }

    @Override
    public Container addProperty(String propertyName, Object propertyValue) {
        return delegate.addProperty(propertyName, propertyValue);
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
    public <T> T get(Class<T> clazz) {
        return delegate.get(clazz);
    }

    @Override
    public <T> T get(Type type) {
        return delegate.get(type);
    }

    @Override
    public <T> T get(SerializableKey key) {
        return delegate.get(key);
    }
}
