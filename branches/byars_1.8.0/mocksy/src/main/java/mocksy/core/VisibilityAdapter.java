package mocksy.core;

/**
 *
 * User: reesbyars
 * Date: 9/13/12
 * Time: 10:22 AM
 * <p/>
 * VisibilityAdapter
 */
public class VisibilityAdapter {

    public static <I, T extends I> T mockify(T target, Class<I> targetInterface) throws DelegationException {
        return DelegationHandlerImpl.getProxy(target, targetInterface);
    }

    public static <I, T extends I> DelegationHandler<T> delegate(T target, Class<I> targetInterface, boolean requireProxy) throws DelegationException {
        return DelegationHandlerImpl.delegate(target, targetInterface, requireProxy);
    }

    public static void cleanup() {
        DelegationHandlerImpl.cleanup();
    }

}
