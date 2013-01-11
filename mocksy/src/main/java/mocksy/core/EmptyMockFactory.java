package mocksy.core;

import mocksy.Mocksy;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Array;
import java.lang.reflect.Method;

/**
 * User: reesbyars
 * Date: 9/17/12
 * Time: 8:21 PM
 * <p/>
 * EmptyMockFactory
 */
public class EmptyMockFactory {

    @SuppressWarnings("unchecked")
    public static <T> T create(Class<T> classToMock) {
        return Mocksy.mockify((T) Enhancer.create(classToMock, new MethodInterceptor() {
            @Override
            public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {

                Class<?> declaringClass = method.getDeclaringClass();
                String methodName = method.getName();

                if (declaringClass == Object.class) {
                    if ("equals".equals(methodName)) {
                        return o == objects[0];
                    }
                    else if ("hashCode".equals(methodName)) {
                        return System.identityHashCode(o);
                    }
                    else if ("toString".equals(methodName)) {
                        return o.getClass().getName() + '@' + Integer.toHexString(System.identityHashCode(o));
                    }
                }
                return getMockValue(method.getReturnType());
            }
        }), classToMock);
    }

    private static Object getMockValue(Class<?> type) {
        Object value = null;
        if (type.isArray()) {
            value = Array.newInstance(type.getComponentType(), 1);
        } else if (type != void.class && type.isPrimitive()) {
            value = resolvePrimitive(type);
        }
        return value;
    }

    private static Object resolvePrimitive(Class<?> type) {
        if (type == byte.class) {
            return (byte) 1;
        } else if (type == char.class) {
            return '\1';
        } else if (type == boolean.class) {
            return true;
        } else if (type == int.class) {
            return 1;
        } else if (type == long.class) {
            return 1L;
        } else if (type == double.class) {
            return 1.0;
        } else if (type == float.class) {
            return 1.0F;
        } else {
            return (short) 1;
        }
    }

}
