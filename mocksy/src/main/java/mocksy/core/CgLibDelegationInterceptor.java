package mocksy.core;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * an interceptor that uses a cglib enhancer to create proxies.  cglib can proxy just about anything, including concrete classes.
 * <P>
 * User: reesbyars
 * Date: 9/11/12
 * Time: 9:50 PM
 * <p/>
 * CgLibDelegationInterceptor
 */
public class CgLibDelegationInterceptor<T> extends AbstractDelegationInterceptor<T> implements MethodInterceptor {

    private static final Logger LOG = LoggerFactory.getLogger(CgLibDelegationInterceptor.class);

    private CgLibDelegationInterceptor(T realObject) {
        super(realObject);
    }

    /**
     * intercepts calls and delegates them to any assigned delegate
     *
     * @param o
     * @param method
     * @param args
     * @param methodProxy
     * @return
     * @throws Throwable
     */
    @Override
    public Object intercept(Object o, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {

        Object result;
        Method delegateMethod = this.getDelegateMethod(method);

        if (delegateMethod != null) {
            result = delegateMethod.invoke(delegate, args);
        } else {
            result = methodProxy.invoke(realObject, args);
        }

        return result;
    }

    /**
     * used by the {@link DelegationInterceptor.Factory} to create instances of this interceptor
     *
     * @param target
     * @param targetInterface
     * @param <I>
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    protected static <I, T extends I> DelegationInterceptor<T> create(T target, Class<I> targetInterface) {
        CgLibDelegationInterceptor<T> interceptor = new CgLibDelegationInterceptor<T>(target);
        if (targetInterface != null) {
            interceptor.proxy = (T) Enhancer.create(targetInterface, interceptor);
        } else if (!Modifier.isFinal(target.getClass().getModifiers())) {
            interceptor.proxy = (T) Enhancer.create(target.getClass(), interceptor);
        } else {
            LOG.warn("Class is final, cannot proxy directly.  Proxying super class and implementing all interfaces.  Will work for some cases.  Why a final class?!?!");
            interceptor.proxy = (T) Enhancer.create(target.getClass().getSuperclass(), target.getClass().getInterfaces(), interceptor);
        }
        return interceptor;
    }
}
