package com.github.overengineer.scope.container;

import com.github.overengineer.scope.CommonConstants;
import com.github.overengineer.scope.CommonModule;
import com.github.overengineer.scope.monitor.DefaultSchedulerProvider;
import com.github.overengineer.scope.monitor.ScheduledExecutorTimeoutMonitor;
import com.github.overengineer.scope.monitor.SchedulerProvider;
import com.github.overengineer.scope.monitor.TimeoutMonitor;
import com.github.overengineer.scope.testutil.ConcurrentExecutionAssistant;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

/**
 *
 */
public class DefaultContainerTest {

    @Test
    public void testLoadModule() {

        Container container = ContainerBuilder.begin().build();

        container.loadModule(new CommonModule());

        TimeoutMonitor monitor = container.get(TimeoutMonitor.class);

        assertNotNull(monitor);

    }

    @Test
    public void testVerify_positive() throws WiringException {

        Container container = ContainerBuilder.begin().build();

        container.verify();

        container.loadModule(new CommonModule());

        container.verify();

    }

    @Test(expected = WiringException.class)
    public void testVerify_negative() throws WiringException {

        Container container = ContainerBuilder.begin().build();

        container.add(TimeoutMonitor.class, ScheduledExecutorTimeoutMonitor.class);

        container.addProperty(CommonConstants.Properties.MONITORING_FREQUENCY, 4L);

        container.verify();

    }

    @Test
    public void testAddAndGetComponent() {

        Container container = ContainerBuilder.begin().build();

        container.add(SchedulerProvider.class, DefaultSchedulerProvider.class);

        container.addProperty(CommonConstants.Properties.MONITORING_THREAD_POOL_SIZE, 4);

        SchedulerProvider provider = container.get(SchedulerProvider.class);

        assertTrue(provider instanceof DefaultSchedulerProvider);

        container.add(IConstructorTest.class, ConstructorTest.class);

        assertEquals(provider, ((ConstructorTest) container.get(IConstructorTest.class)).provider);

    }

    @Test
    public void testAddAndGetInstance() {

        Container container = ContainerBuilder.begin().build();

        SchedulerProvider given = new DefaultSchedulerProvider();

        container.addInstance(SchedulerProvider.class, given);

        container.addProperty(CommonConstants.Properties.MONITORING_THREAD_POOL_SIZE, 4);

        SchedulerProvider received = container.get(SchedulerProvider.class);

        assertEquals(given, received);

    }

    @Test
    public void testAddAndGetProperty() {

        Container container = ContainerBuilder.begin().build();

        container.addProperty("test", 69L);

        assertEquals((Long) 69L, container.getProperty(Long.TYPE, "test"));

    }

    @Test(expected = Assertion.class)
    public void testAddListener() {

        Container container = ContainerBuilder.begin().build();

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

        HotSwappableContainer container = ContainerBuilder
                .begin()
                .withJdkProxies()
                .build();

        container
                .add(ICyclicRef.class, CyclicTest.class)
                .add(ICyclicRef2.class, CyclicTest2.class)
                .add(ICyclicRef3.class, CyclicTest3.class);

        ICyclicRef c = container.get(ICyclicRef.class);

        assertNotNull(c.getRef());

        c.getRef().getRef();

        ICyclicRef2 c2 = container.get(ICyclicRef2.class);

        assertNotNull(c2.getRef());

        assertEquals(2, c2.getRef().calls());

        assertEquals(2, c2.calls()); //one because its a prototype

        ICyclicRef3 c3 = container.get(ICyclicRef3.class);

        assertNotNull(c3.getRef());

        assertEquals(1, c.getRef().calls());

    }

    @Test
    public void testCyclicRef2() {

        HotSwappableContainer container = ContainerBuilder
                .begin()
                .withJdkProxies()
                .build();

        container
                .add(ICyclicRef.class, CyclicTest.class)
                .add(ICyclicRef2.class, CyclicTest2.class)
                .add(ICyclicRef3.class, CyclicTest3.class);

        ICyclicRef c = container.get(ICyclicRef.class);

        assertEquals(1, c.getRef().getRef().getRef().calls());

    }

    @Test
    public void testHotSwapping() throws HotSwapException {

        HotSwappableContainer container = ContainerBuilder
                .begin()
                .withJdkProxies()
                .build();

        container
                .add(ICyclicRef.class, CyclicTest.class)
                .add(ICyclicRef2.class, CyclicTest2.class)
                .add(ICyclicRef3.class, CyclicTest3.class);

        ICyclicRef c = container.get(ICyclicRef.class);

        container.swap(ICyclicRef.class, CyclicTestHot.class);

        assertEquals(69, c.calls());

    }

    @Test
    public void testSpeed() throws Exception {

        int threads = 1;
        long duration = 1000;

        final HotSwappableContainer container = ContainerBuilder
                .begin()
                .withJdkProxies()
                .build();

        container
                .add(IBean.class, Bean.class)
                .add(IBean2.class, Bean2.class);

        new ConcurrentExecutionAssistant.TestThreadGroup(new ConcurrentExecutionAssistant.Execution() {
            @Override
            public void execute() throws HotSwapException {
                container.get(IBean.class);
            }
        }, threads).run(duration, "double prototype");

        final Container container2 = ContainerBuilder
                .begin()
                .withJdkProxies()
                .build();

        container2.loadModule(new CommonModule());

        new ConcurrentExecutionAssistant.TestThreadGroup(new ConcurrentExecutionAssistant.Execution() {
            @Override
            public void execute() throws HotSwapException {
                container2.get(SchedulerProvider.class);
            }
        }, threads).run(duration, "singleton");

    }

    interface IConstructorTest {}

    @Prototype
    public static class ConstructorTest implements IConstructorTest {
        SchedulerProvider provider;
        public ConstructorTest(SchedulerProvider provider) {
            this.provider = provider;
        }
    }

    public interface ICyclicRef {
        ICyclicRef3 getRef();
        int calls();
    }

    @Prototype
    public interface ICyclicRef2 {
        ICyclicRef getRef();
        int calls();
    }

    public interface ICyclicRef3 {
        ICyclicRef2 getRef();
        int calls();
    }

    public static class CyclicTest implements ICyclicRef {
        ICyclicRef3 cyclicTest3;
        int calls = 0;
        public CyclicTest(ICyclicRef3 cyclicTest3) {
            cyclicTest3.calls();
            this.cyclicTest3 = cyclicTest3;
        }

        @Override
        public ICyclicRef3 getRef() {
            calls++;
            return cyclicTest3;
        }

        @Override
        public int calls() {
            return calls;
        }
    }

    public static class CyclicTestHot extends CyclicTest {
        public CyclicTestHot(ICyclicRef3 cyclicTest3, ICyclicRef self) {
            super(cyclicTest3);
        }
        @Override
        public int calls() {
            return 69;
        }
    }

    @Prototype
    public static class CyclicTest2 implements ICyclicRef2 {
        ICyclicRef cyclicTest;
        int calls = 0;
        public CyclicTest2(ICyclicRef cyclicTest) {
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

    @Prototype
    public static class CyclicTest3 implements ICyclicRef3 {
        ICyclicRef2 cyclicTest;
        int calls = 0;
        public CyclicTest3(ICyclicRef2 cyclicTest) {
            this.cyclicTest = cyclicTest;
        }

        @Override
        public ICyclicRef2 getRef() {
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
