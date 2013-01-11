package mocksy.core;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * User: reesbyars
 * Date: 9/17/12
 * Time: 11:36 AM
 * <p/>
 * Mock
 */
public abstract class Mock<T> {

    private Map<Method, Integer> invocationMap = new HashMap<Method, Integer>();
    protected T realObject;
    protected T proxy;

    public T real() {
        return realObject;
    }

    public T proxy() {
        return proxy;
    }

    protected void setRealObject(T realObject) {
        this.realObject = realObject;
    }

    protected void notify(Method method) {
        Integer count = this.invocationMap.get(method);
        if (count == null) {
            count = 0;
        }
        this.invocationMap.put(method, ++count);
    }

    public void verify() {
        for (Method method : this.getClass().getDeclaredMethods()) {
        	
            Verify verify = method.getAnnotation(Verify.class);
            if (verify != null) {

                Integer count = this.invocationMap.get(method);
                if (count == null) {
                    count = 0;
                }

                int calls = verify.calls();
                int max = verify.maxCalls();
                int min = verify.minCalls();

                if (max != -1) {
                	assertTrue( count <= max, "The method " + method.getName() + " of " + realObject.getClass() + " was invoked " + count + " times.  Expected only " + max + "." );
                }

                if (min != -1) {
                	assertTrue( count >= min, "The method " + method.getName() + " of " + realObject.getClass() + " was invoked " + count + " times.  Expected at least " + min + ".");
                }

                if (calls != -1) {
                	assertTrue( count == calls, "The method " + method.getName() + " of " + realObject.getClass() + " was invoked " + count + " times.  Expected exactly " + calls + ".");
                } else if (calls + max + min == -3) {
                    //if no values set, default to requiring exactly one invocation
                	assertTrue( count == 1, "The method " + method.getName() + " of " + realObject.getClass() + " was invoked " + count + " times.  Expected exactly " + 1 + ".");
                }

            }
        }
    }
    
    private static void assertTrue(boolean expression, String message) {
    	if (!expression) throw new RuntimeException(new VerificationException(message));
    }
    
    public static class VerificationException extends Exception {
		private static final long serialVersionUID = 2039031849374188573L;
		private VerificationException(String message) {
			super(message);
		}
    }

}
