package mocksy.core;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * User: reesbyars
 * Date: 9/11/12
 * Time: 9:51 PM
 * <p/>
 * AbstractDelegationInterceptor
 */
public abstract class AbstractDelegationInterceptor<T> implements DelegationInterceptor<T> {

    protected Map<Method, Method> delegateMethodCache = Collections.emptyMap();
    protected Class<?> delegateClass;
    protected final Class<?> realObjectClass;
    protected final T realObject;
    protected Object delegate;
    protected T proxy;

    protected AbstractDelegationInterceptor(T realObject) {
        this.realObject = realObject;
        this.realObjectClass = realObject.getClass();
        this.setDelegate(realObject);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void setDelegate(Object delegate) {
        Class<?> newDelegateClass = delegate.getClass();
        if (newDelegateClass != this.delegateClass) {
            this.delegateClass = newDelegateClass;
            this.delegateMethodCache.clear();
            this.delegateMethodCache = new HashMap<Method, Method>();
            if (delegate instanceof Mock) {
                ((Mock<T>) delegate).setRealObject(this.realObject);
            }
        }
        this.delegate = delegate;
    }

    @Override
    public T removeDelegate() {
        this.delegate = this.realObject;
        this.delegateClass = realObjectClass;
        this.delegateMethodCache.clear();
        this.delegateMethodCache = Collections.emptyMap();
        return this.proxy;
    }

    @Override
    public T getProxy() {
        return this.proxy;
    }

    @Override
    public T getRealObject() {
        return this.realObject;
    }

    @Override
    public Object getDelegate() {
        return this.delegate;
    }

    @SuppressWarnings("unchecked")
    protected Method getDelegateMethod(Method realMethod) {

        if (this.delegateClass == this.realObjectClass) {
            return null;
        }

        Method method = this.delegateMethodCache.get(realMethod);
        if (!this.delegateMethodCache.containsKey(realMethod)) {
            String realMethodName = realMethod.getName();
            Class<?>[] realMethodParameterTypes = realMethod.getParameterTypes();
            try {
                method = this.delegateClass.getMethod(realMethodName, realMethodParameterTypes);
                method.setAccessible(true);
            } catch (NoSuchMethodException e) {
                for (Method candidateMethod : this.delegateClass.getDeclaredMethods()) {
                    if (realMethodName.equals(candidateMethod.getName())) {
                        Class<?>[] candidateMethodParameterTypes =  candidateMethod.getParameterTypes();
                        if (realMethodParameterTypes.length == candidateMethodParameterTypes.length) {
                            boolean winner = true;
                            for (int i = 0; i < realMethodParameterTypes.length; i++) {
                                if (!realMethodParameterTypes[i].isAssignableFrom(candidateMethodParameterTypes[i])) {
                                    winner = false;
                                    break;
                                }
                            }
                            if (winner) {
                                method = candidateMethod;
                                method.setAccessible(true);
                                break;
                            }
                        }
                    }
                }
            }
            this.delegateMethodCache.put(realMethod, method);
        }
        if (this.delegate instanceof Mock) {
            ((Mock<T>) this.delegate).notify(method);
        }
        return method;
    }

}
