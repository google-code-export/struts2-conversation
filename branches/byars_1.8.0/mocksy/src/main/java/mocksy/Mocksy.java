package mocksy;

import mocksy.core.*;

/**
 * A sort of "static interface" to the Mocksy delegation framework that can be imported into test classes
 * for simple use of the framework.  Primarily serves to allow fine-tuned, simple, inline mocking/delegation of autowired dependencies
 * in tests run with the {@link org.springframework.test.context.junit4.SpringJUnit4ClassRunner SpringJUnit4ClassRunner}.
 * This is achieved by adding the {@link mocksy.integration.SpringMocksyPostProcessor SpringMocksyPostProcessor}
 * to a test context.xml and then using the
 * {@link #delegate(Object)} method to assign/delegate mock implementations of methods to your Spring manged beans.  Yes,
 * its not really as good as JMockit, but I likes it, and its mines.
 * <p/>
 * Mocksy also supports multi-threaded, concurrent testing without worry of muddying your delegations (not supported in JMockit, whose
 * byte-code instrumentation approach is not thread safe).
 * <p/>
 * User: reesbyars
 * Date: 9/11/12
 * Time: 6:54 PM
 * <p/>
 * Mocksy
 */
public class Mocksy {

    /**
     * get a delegatable proxy of the given object.  this can be done automatically for all autowired beans
     * by using the {@link mocksy.integration.SpringMocksyPostProcessor SpringMocksyPostProcessor}.
     *
     * @param target
     * @param <T>
     * @return
     * @throws mocksy.core.DelegationException
     */
    public static <T> T mockify(T target) throws DelegationException {
        return mockify(target, null);
    }

    /**
     * get a delegatable proxy of the given object.  this can be done automatically for all autowired beans
     * by using the {@link mocksy.integration.SpringMocksyPostProcessor SpringMocksyPostProcessor}.
     *
     * @param target
     * @param targetInterface
     * @param <I>
     * @param <T>
     * @return
     * @throws DelegationException
     */
    public static <I, T extends I> T mockify(T target, Class<I> targetInterface) throws DelegationException {
        return VisibilityAdapter.mockify(target, targetInterface);
    }

    /**
     * returns the delegation handler for the given proxy which can then be used to assign a delegate in the form:
     * <pre>
     *
     *     delegate(myObject).to(myMock);
     *
     * </pre>
     * after which calls to <i>myObject</i> will be delegated to <i>myMock</i>.  the mock can be of any class and the delegate
     * will be used for any methods whose signatures it has in common with the <i>myObject</i>, while <i>myObject</i>
     * will still be called for methods that the mock does not provide.  it is probably most useful for using anonymous classes
     * in the form:
     * <pre>
     *
     *      delegate(this.metricService).to(new Object() {
     *          void saveMetric(BusinessMetrics metric) {
     *              System.out.println("hello proxied mocksy world!");
     *          }
     *      });
     *
     *      this.metricService.saveMetric(mockMetric);
     *
     * </pre>
     * which would just print "hello proxied mocksy world!".
     * <p/>
     * if used with the {@link mocksy.integration.SpringMocksyPostProcessor SpringMocksyPostProcessor},
     * Spring beans can be autowired into the test class
     * and be dynamically mocked/delegated.  in this way it can help to facilitate integration testing by
     * providing a very light-weight mechanism for quickly mocking/delegating
     * finely targeted pieces without spilling over into integration concerns like JMockit mocks can do.
     *
     * @param target
     * @param <T>
     * @return
     * @throws DelegationException
     */
    public static <T> DelegationHandler<T> delegate(T target) throws DelegationException {
        return delegate(target, null);
    }

    /**
     * remove the current delegate for the given proxy
     *
     * @param target
     * @param <T>
     * @return
     * @throws DelegationException
     */
    public static <T> T remove(T target) throws DelegationException {
        return delegate(target, null).remove();
    }

    /**
     * gets the real object from behind a proxy
     *
     * @param target
     * @param <T>
     * @return the real object behind the given proxy.  if the given object is not a proxy, it just returns that object
     * @throws DelegationException
     */
    public static <T> T real(T target) throws DelegationException {
        return delegate(target, null, false).getRealObject();
    }

    public static <I, T extends I> DelegationHandler<T> delegate(T target, Class<I> targetInterface) throws DelegationException {
        return delegate(target, targetInterface, true); //true to enforce good practice
    }

    public static <I, T extends I> DelegationHandler<T> delegate(T target, Class<I> targetInterface, boolean requireProxy) throws DelegationException {
        return VisibilityAdapter.delegate(target, targetInterface, requireProxy);
    }

    /**
     * cleans up delegation resources to reduce memory imprint.  automatically called after each test class from
     * {@link mocksy.integration.SpringMocksyPostProcessor SpringMocksyPostProcessor}.
     */
    public static void cleanup() {
        VisibilityAdapter.cleanup();
    }

    public static void verify(Object proxy) throws DelegationException {
        Object delegate = delegate(proxy).getDelegate();
        if (delegate instanceof Mock) {
            ((Mock<?>) delegate).verify();
        }
    }

    public static <T> T newEmptyMock(Class<T> classToMock) {
        return EmptyMockFactory.create(classToMock);
    }

    public static <T> Spy.Infiltrator set(T value) throws DelegationException {
        return Spy.set(value);
    }

    public static <T> Spy.Hijacker<T> get(Class<T> classToGet) {
        return Spy.get(classToGet);
    }

}