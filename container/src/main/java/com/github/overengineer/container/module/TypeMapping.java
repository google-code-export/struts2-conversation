package com.github.overengineer.container.module;

import com.github.overengineer.container.key.GenericKey;
import com.github.overengineer.container.key.SerializableKey;
import com.github.overengineer.container.scope.Scope;
import com.github.overengineer.container.scope.Scopes;

import java.util.LinkedList;
import java.util.List;

/**
 * @author rees.byars
 */
public class TypeMapping<T> implements Mapping<T>, MutableMapping<T> {

    private final Class<T> implementationType;
    private Scope scope = Scopes.SINGLETON;
    private String name;
    private List<Class<?>> targetClasses = new LinkedList<Class<?>>();
    private List<SerializableKey> targetKeys = new LinkedList<SerializableKey>();

    public TypeMapping(Class<T> implementationType) {
        this.implementationType = implementationType;
        targetClasses.add(implementationType);
    }

    @Override
    public MutableMapping<T> forType(Class<? super T> targetClass) {
        targetClasses.add(targetClass);
        return this;
    }

    @Override
    public MutableMapping<T> forType(GenericKey<? super T> targetKey) {
        targetKeys.add(targetKey);
        return this;
    }

    @Override
    public MutableMapping<T> withScope(Scope scope) {
        this.scope = scope;
        return this;
    }

    @Override
    public MutableMapping<T> withName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public Class<T> getImplementationType() {
        return implementationType;
    }

    @Override
    public List<Class<?>> getTargetClasses() {
        return targetClasses;
    }

    @Override
    public List<SerializableKey> getTargetKeys() {
        return targetKeys;
    }

    @Override
    public Scope getScope() {
        return scope;
    }

    @Override
    public String getName() {
        return name;
    }

}
