package com.github.overengineer.scope.container;

public class SingletonInjectionStrategy<T> implements InjectionStrategy<T> {
	
	private T component;
	
	public SingletonInjectionStrategy(InjectionContext<T> injectionContext) {
		component = injectionContext.getSingletonComponent();
		for (Injector<T> injector : Injector.CacheBuilder.build(injectionContext.getImplementationType())) {
			injector.inject(component, injectionContext.getContainer());
		}
		if (component instanceof PostConstructable) {
			((PostConstructable) component).init();
		}
	}

	@Override
	public T getComponent() {
		return component;
	}

}
