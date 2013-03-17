package com.github.overengineer.scope.container;

public interface InjectionStrategy<T> {
	
	T getComponent();
	
	class Factory {
		public static <T> InjectionStrategy<T> create(InjectionContext<T> context) {
			if (context.getImplementationType().isAnnotationPresent(Prototype.class)) {
				return new PrototypeInjectionStrategy<T>(context);
			} else {
				return new SingletonInjectionStrategy<T>(context);
			}
		}
	}

}
