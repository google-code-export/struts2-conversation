package com.github.overengineer.scope.container;

import com.github.overengineer.scope.CommonConstants;
import com.github.overengineer.scope.CommonModule;
import com.github.overengineer.scope.monitor.DefaultSchedulerProvider;
import com.github.overengineer.scope.monitor.ScheduledExecutorTimeoutMonitor;
import com.github.overengineer.scope.monitor.SchedulerProvider;
import com.github.overengineer.scope.monitor.TimeoutMonitor;
import com.github.overengineer.scope.testutil.ConcurrentExecutionAssistant;
import com.google.inject.*;
import com.google.inject.Injector;
import org.junit.Test;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.behaviors.Storing;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

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

        container.get(ICyclicRef2.class);
        container.get(ICyclicRef2.class);
        container.get(ICyclicRef2.class);
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

    int threads = 4;
    long duration = 2000;

    @Test
    public void testPlainPrototypingSpeed() throws Exception {

        final Container container3 = ContainerBuilder
                .begin()
                .build()
                .add(IBean.class, Bean.class)
                .add(IBean2.class, Bean2.class);

        new ConcurrentExecutionAssistant.TestThreadGroup(new ConcurrentExecutionAssistant.Execution() {
            @Override
            public void execute() throws HotSwapException {
                container3.get(IBean.class);
            }
        }, threads).run(duration, "my plain prototype");

        final PicoContainer picoContainer = new DefaultPicoContainer()
                .addComponent(IBean.class, Bean.class)
                .addComponent(IBean2.class, Bean2.class);

        new ConcurrentExecutionAssistant.TestThreadGroup(new ConcurrentExecutionAssistant.Execution() {
            @Override
            public void execute() throws HotSwapException {
                picoContainer.getComponent(IBean.class);
            }
        }, threads).run(duration, "pico plain prototype");

        final Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(IBean.class).to(Bean.class);
                bind(IBean2.class).to(Bean2.class);
            }
        });

        new ConcurrentExecutionAssistant.TestThreadGroup(new ConcurrentExecutionAssistant.Execution() {
            @Override
            public void execute() throws HotSwapException {
                injector.getInstance(IBean.class);
            }
        }, threads).run(duration, "guice plain prototypes");

        final ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring-plain-prototype.xml");

        new ConcurrentExecutionAssistant.TestThreadGroup(new ConcurrentExecutionAssistant.Execution() {
            @Override
            public void execute() throws HotSwapException {
                applicationContext.getBean(Bean.class);
            }
        }, threads).run(duration, "spring plain prototypes");

    }

    @Test
    public void testSingletonSpeed() throws Exception {

        final Container container2 = ContainerBuilder
                .begin()
                .withJdkProxies()
                .build()
                .add(ISingleton.class, Singleton.class);

        new ConcurrentExecutionAssistant.TestThreadGroup(new ConcurrentExecutionAssistant.Execution() {
            @Override
            public void execute() throws HotSwapException {
                container2.get(ISingleton.class);
            }
        }, threads).run(duration, "my singleton");

        final PicoContainer picoContainer3 = new DefaultPicoContainer(new Storing())
                .addComponent(ISingleton.class, Singleton.class);

        new ConcurrentExecutionAssistant.TestThreadGroup(new ConcurrentExecutionAssistant.Execution() {
            @Override
            public void execute() throws HotSwapException {
                picoContainer3.getComponent(ISingleton.class);
            }
        }, threads).run(duration, "pico singleton");

        final Injector injector3 = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(ISingleton.class).to(Singleton.class).in(Scopes.SINGLETON);
            }
        });

        new ConcurrentExecutionAssistant.TestThreadGroup(new ConcurrentExecutionAssistant.Execution() {
            @Override
            public void execute() throws HotSwapException {
                injector3.getInstance(ISingleton.class);
            }
        }, threads).run(duration, "guice singleton");

        final ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring-singleton.xml");

        new ConcurrentExecutionAssistant.TestThreadGroup(new ConcurrentExecutionAssistant.Execution() {
            @Override
            public void execute() throws HotSwapException {
                applicationContext.getBean(Singleton.class);
            }
        }, threads).run(duration, "spring singleton");

    }

    @Test
    public void testCyclicRefSpeed() throws Exception {

        final Container container = ContainerBuilder
                .begin()
                .withJdkProxies()
                .build()
                .add(ICyclicRef.class, PCyclicTest.class)
                .add(ICyclicRef2.class, CyclicTest2.class)
                .add(ICyclicRef3.class, CyclicTest3.class);

        new ConcurrentExecutionAssistant.TestThreadGroup(new ConcurrentExecutionAssistant.Execution() {
            @Override
            public void execute() throws HotSwapException {
                container.get(ICyclicRef2.class);
            }
        }, threads).run(duration, "my cyclic refs");

        final Injector injector2 = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(ICyclicRef.class).to(PCyclicTest.class);
                bind(ICyclicRef2.class).to(CyclicTest2.class);
                bind(ICyclicRef3.class).to(CyclicTest3.class);
            }
        });

        new ConcurrentExecutionAssistant.TestThreadGroup(new ConcurrentExecutionAssistant.Execution() {
            @Override
            public void execute() throws HotSwapException {
                injector2.getInstance(ICyclicRef2.class);
            }
        }, threads).run(duration, "guice cyclic refs");

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
        @Inject
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

    @Prototype
    public static class PCyclicTest extends CyclicTest {
        @Inject
        public PCyclicTest(ICyclicRef3 cyclicTest3, ICyclicRef self) {
            super(cyclicTest3);
        }
        @Override
        public int calls() {
            return 69;
        }
    }

    public static class CyclicTestHot extends CyclicTest {
        @Inject
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
        @Inject
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
        @Inject
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

    interface ISingleton{}

    public static class Singleton implements ISingleton {}

    public static class Assertion extends RuntimeException {}

}
