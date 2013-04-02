package com.github.overengineer.scope.container;

import com.github.overengineer.scope.CommonConstants;
import com.github.overengineer.scope.CommonModule;
import com.github.overengineer.scope.monitor.DefaultSchedulerProvider;
import com.github.overengineer.scope.monitor.ScheduledExecutorTimeoutMonitor;
import com.github.overengineer.scope.monitor.SchedulerProvider;
import com.github.overengineer.scope.monitor.TimeoutMonitor;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 */
public class DefaultContainerTest {

    @Test
    public void testLoadModule() {

        Container container = ContainerBuilder.build();

        container.loadModule(new CommonModule());

        TimeoutMonitor monitor = container.get(TimeoutMonitor.class);

        assertNotNull(monitor);

    }

    @Test
    public void testVerify_positive() throws WiringException {

        Container container = ContainerBuilder.build();

        container.verify();

        container.loadModule(new CommonModule());

        container.verify();

    }

    @Test(expected = WiringException.class)
    public void testVerify_negative() throws WiringException {

        Container container = ContainerBuilder.build();

        container.add(TimeoutMonitor.class, ScheduledExecutorTimeoutMonitor.class);

        container.addProperty(CommonConstants.Properties.MONITORING_FREQUENCY, 4L);

        container.verify();

    }

    @Test
    public void testAddAndGetComponent() {

        Container container = ContainerBuilder.build();

        container.add(SchedulerProvider.class, DefaultSchedulerProvider.class);

        container.addProperty(CommonConstants.Properties.MONITORING_THREAD_POOL_SIZE, 4);

        SchedulerProvider provider = container.get(SchedulerProvider.class);

        assertTrue(provider instanceof DefaultSchedulerProvider);

        container.add(ConstructorTest.class, ConstructorTest.class);

        assertEquals(provider, container.get(ConstructorTest.class).provider);

    }

    @Test
    public void testAddAndGetInstance() {

        Container container = ContainerBuilder.build();

        SchedulerProvider given = new DefaultSchedulerProvider();

        container.addInstance(SchedulerProvider.class, given);

        container.addProperty(CommonConstants.Properties.MONITORING_THREAD_POOL_SIZE, 4);

        SchedulerProvider received = container.get(SchedulerProvider.class);

        assertEquals(given, received);

    }

    @Test
    public void testAddAndGetProperty() {

        Container container = ContainerBuilder.build();

        container.addProperty("test", 69L);

        assertEquals((Long) 69L, container.getProperty(Long.TYPE, "test"));

    }

    @Test(expected = Assertion.class)
    public void testAddListener() {

        Container container = ContainerBuilder.build();

        container.add(SchedulerProvider.class, DefaultSchedulerProvider.class);

        container.addProperty(CommonConstants.Properties.MONITORING_THREAD_POOL_SIZE, 4);

        container.addListener(new ComponentInitializationListener() {
            @Override
            public <T> T onInitialization(T component) {
                throw new Assertion();
            }
        });

        container.get(SchedulerProvider.class);

    }

    @Test
    public void testCyclicRef() {

        Container container = ContainerBuilder.withProxies().build();

        container.add(ICyclicRef.class, CyclicTest.class);

        container.add(ICyclicRef2.class, CyclicTest2.class);

        ICyclicRef c = container.get(ICyclicRef.class);

        assertNotNull(c.getRef());

        c.getRef().getRef();

        ICyclicRef2 c2 = container.get(ICyclicRef2.class);

        assertNotNull(c2.getRef());

        assertTrue(c.calls() == 2);

        assertTrue(c2.calls() == 1); //one because its a prototype

    }

    @Prototype
    public static class ConstructorTest {
        SchedulerProvider provider;
        public ConstructorTest(SchedulerProvider provider) {
            this.provider = provider;
        }
    }

    public interface ICyclicRef {
        ICyclicRef2 getRef();
        int calls();
    }

    public interface ICyclicRef2 {
        ICyclicRef getRef();
        int calls();
    }

    @Prototype
    public static class CyclicTest implements ICyclicRef {
        ICyclicRef2 cyclicTest2;
        int calls = 0;
        public CyclicTest(ICyclicRef2 cyclicTest2) {
            this.cyclicTest2 = cyclicTest2;
        }

        @Override
        public ICyclicRef2 getRef() {
            calls++;
            return cyclicTest2;
        }

        @Override
        public int calls() {
            return calls;
        }
    }

    @Prototype
    public static class CyclicTest2 implements ICyclicRef2 {
        ICyclicRef cyclicTest;
        int calls = 0;
        @Component
        public void setCyclicTest(ICyclicRef cyclicTest) {
            this.cyclicTest = cyclicTest;
        }

        @Override
        public ICyclicRef getRef() {
            calls++;
            return cyclicTest;
        }

        @Override
        public int calls() {
            return calls;
        }
    }

    public static class Assertion extends RuntimeException {}

}
