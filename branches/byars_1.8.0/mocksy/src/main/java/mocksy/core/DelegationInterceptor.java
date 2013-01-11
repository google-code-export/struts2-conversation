package mocksy.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User: reesbyars
 * Date: 9/11/12
 * Time: 9:27 PM
 * <p/>
 * DelegationInterceptor
 */
public interface DelegationInterceptor<T> {

    void setDelegate(Object delegate);

    /**
     * removes the delegate
     * @return the proxy
     */
    T removeDelegate();

    T getProxy();

    T getRealObject();

    Object getDelegate();

    /**
     * a factory class used to decouple the interceptor implementation from the delegation handler in the absence
     * of dependency injection
     */
    static class Factory {

        static final Logger LOG = LoggerFactory.getLogger(Factory.class);

        static boolean useCgLib;

        static {
            try {
                Class.forName("net.sf.cglib.proxy.Enhancer", false, DelegationInterceptor.class.getClassLoader());
                LOG.info("CgLib was found on the classpath.  CgLib will be used for proxy creation.");
                useCgLib = true;
            } catch (ClassNotFoundException e) {
                LOG.warn("Did not find CgLib on the classpath.  Using native JDK Proxy for proxy creation.  Will work fine for most cases, but will not be able to proxy concrete classes that do not implement interfaces.");
                useCgLib = false;
            }
        }

        protected static <I, T extends I> DelegationInterceptor<T> create(T target, Class<I> targetInterface) throws DelegationException {
            if (useCgLib) {
                return CgLibDelegationInterceptor.create(target, targetInterface);
            } else {
                return JdkDelegationInterceptor.create(target, targetInterface);
            }
        }
    }

}
