package mocksy.core;

import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

/**
 * User: reesbyars
 * Date: 9/11/12
 * Time: 9:37 PM
 * <p/>
 * DelegationHandlerImpl
 */
public class DelegationHandlerImpl<T> implements DelegationHandler<T> {

    protected static final ThreadLocal<Map<Class<?>, DelegationHandler<?>>> localHandlerCache = new ThreadLocal<Map<Class<?>, DelegationHandler<?>>>();

    protected DelegationInterceptor<T> interceptor;

    private DelegationHandlerImpl() {}

    @Override
    public T to(Object delegate) {
        this.interceptor.setDelegate(delegate);
        return this.interceptor.getProxy();
    }

    @Override
    public T remove() {
        this.interceptor.removeDelegate();
        return this.interceptor.getProxy();
    }

    @Override
    public T getProxy() {
        return this.interceptor.getProxy();
    }

    @Override
    public T getRealObject() {
        return this.interceptor.getRealObject();
    }

    @Override
    public Object getDelegate() {
        return this.interceptor.getDelegate();
    }

    protected static <I, T extends I> T getProxy(T target, Class<I> targetInterface) throws DelegationException {
        return resolveHandler(target, targetInterface).getProxy();
    }

    protected static <I, T extends I> DelegationHandler<T> delegate(T target, Class<I> targetInterface, boolean requireProxy) throws DelegationException {

        if (requireProxy) {
            Class<?> targetClass = target.getClass();
            if (!Proxy.isProxyClass(target.getClass()) && !targetClass.getName().contains("EnhancerByCGLIB")) {
                throw new DelegationException("The target is not a proxy.  A proxy must first be obtained using mockify(target) and then passed as the target.");
            }
        }

        return resolveHandler(target, targetInterface);

    }

    protected static void cleanup() {
        Map<Class<?>, DelegationHandler<?>> handlerCache = localHandlerCache.get();
        if (handlerCache != null) {
            handlerCache.clear();
            localHandlerCache.remove();
        }
    }

    private static <I, T extends I> DelegationHandler<T> resolveHandler(T target, Class<I> targetInterface) throws DelegationException {

        if (target == null) {
            throw new DelegationException("Target cannot be null.  If trying to create an empty proxy of an interface, use the EmptyMockFactory or the Mocksy.newEmptyMock() method.");
        }

        Map<Class<?>, DelegationHandler<?>> handlerCache = localHandlerCache.get();

        if (handlerCache == null) {
            handlerCache = new HashMap<Class<?>, DelegationHandler<?>>();
            localHandlerCache.set(handlerCache);
        }

        @SuppressWarnings("unchecked")
        DelegationHandlerImpl<T> handler = (DelegationHandlerImpl<T>) handlerCache.get(target.getClass());

        if (handler == null) {

            handler = new DelegationHandlerImpl<T>();

            handler.interceptor = DelegationInterceptor.Factory.create(target, targetInterface);

            //associate the handler to work whether given the proxy or the real object
            handlerCache.put(handler.getProxy().getClass(), handler);
            handlerCache.put(target.getClass(), handler);

        }

        return handler;
    }

}
