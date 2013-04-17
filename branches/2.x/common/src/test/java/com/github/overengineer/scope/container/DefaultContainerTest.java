package com.github.overengineer.scope.container;

import com.github.overengineer.scope.CommonConstants;
import com.github.overengineer.scope.CommonModule;
import com.github.overengineer.scope.container.proxy.HotSwapException;
import com.github.overengineer.scope.container.proxy.HotSwappableContainer;
import com.github.overengineer.scope.container.proxy.ProxyModule;
import com.github.overengineer.scope.container.proxy.ProxyUtil;
import com.github.overengineer.scope.container.proxy.aop.*;
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
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

/**
 *
 */
public class DefaultContainerTest {

    @Test
    public void testLoadModule() {

        Container container = new DefaultContainer(new DefaultComponentStrategyFactory());

        container.loadModule(new CommonModule());

        TimeoutMonitor monitor = container.get(TimeoutMonitor.class);

        assertNotNull(monitor);

    }

    @Test
    public void testVerify_positive() throws WiringException {

        Container container = new DefaultContainer(new DefaultComponentStrategyFactory());

        container.verify();

        container.loadModule(new CommonModule());

        container.verify();

    }

    @Test(expected = WiringException.class)
    public void testVerify_negative() throws WiringException {

        Container container = new DefaultContainer(new DefaultComponentStrategyFactory());

        container.add(TimeoutMonitor.class, ScheduledExecutorTimeoutMonitor.class);

        container.addProperty(CommonConstants.Properties.MONITORING_FREQUENCY, 4L);

        container.verify();

    }

    @Test
    public void testAddAndGetComponent() {

        Container container = new DefaultContainer(new DefaultComponentStrategyFactory());

        container.add(SchedulerProvider.class, DefaultSchedulerProvider.class);

        container.addProperty(CommonConstants.Properties.MONITORING_THREAD_POOL_SIZE, 4);

        SchedulerProvider provider = container.get(SchedulerProvider.class);

        assertTrue(provider instanceof DefaultSchedulerProvider);

        container.add(IConstructorTest.class, ConstructorTest.class);

        assertEquals(provider, ((ConstructorTest) container.get(IConstructorTest.class)).provider);

    }

    @Test
    public void testAddAndGetInstance() {

        Container container = new DefaultContainer(new DefaultComponentStrategyFactory());

        SchedulerProvider given = new DefaultSchedulerProvider();

        container.addInstance(SchedulerProvider.class, given);

        container.addProperty(CommonConstants.Properties.MONITORING_THREAD_POOL_SIZE, 4);

        SchedulerProvider received = container.get(SchedulerProvider.class);

        assertEquals(given, received);

    }

    @Test
    public void testAddAndGetProperty() {

        Container container = new DefaultContainer(new DefaultComponentStrategyFactory());

        container.addProperty("test", 69L);

        assertEquals((Long) 69L, container.getProperty(Long.TYPE, "test"));

    }

    @Test(expected = Assertion.class)
    public void testAddListener() {

        Container container = new DefaultContainer(new DefaultComponentStrategyFactory()).addListener(Listener.class)
                .add(SchedulerProvider.class, DefaultSchedulerProvider.class)
                .addProperty(CommonConstants.Properties.MONITORING_THREAD_POOL_SIZE, 4)
                .start();

        container.get(SchedulerProvider.class);

    }

    public static class Listener implements ComponentInitializationListener {
        @Override
        public <T> T onInitialization(T component) {
            throw new Assertion();
        }
    }

    @Test
    public void testCyclicRef() {

        HotSwappableContainer container = new DefaultContainer(new DefaultComponentStrategyFactory())
                .loadModule(new ProxyModule())
                .get(HotSwappableContainer.class);

        container
                .add(ICyclicRef.class, CyclicTest.class)
                .add(ICyclicRef2.class, CyclicTest2.class)
                .add(ICyclicRef3.class, CyclicTest3.class)
        .start();

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

        HotSwappableContainer container = new DefaultContainer(new DefaultComponentStrategyFactory()).loadModule(new ProxyModule()).get(HotSwappableContainer.class);

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

        HotSwappableContainer container = new DefaultContainer(new DefaultComponentStrategyFactory()).loadModule(new ProxyModule()).get(HotSwappableContainer.class);

        container
                .add(ICyclicRef.class, CyclicTest.class)
                .add(ICyclicRef2.class, CyclicTest2.class)
                .add(ICyclicRef3.class, CyclicTest3.class);

        ICyclicRef c = container.get(ICyclicRef.class);

        container.swap(ICyclicRef.class, CyclicTestHot.class);

        assertEquals(69, c.calls());

    }

    @Test(expected = Assertion.class)
    public void testIntercept() throws HotSwapException {

        new DefaultContainer(new DefaultComponentStrategyFactory())
                .loadModule(new AopModule())
                .get(AopContainer.class)
                .addAspect(TestInterceptor.class)
                .addAspect(Metaceptor.class)
                .add(SchedulerProvider.class, DefaultSchedulerProvider.class)
                .add(ICyclicRef3.class, CyclicTest3.class);
    }

    @Pointcut(
            paramterTypes = {Class.class, Class.class},
            annotations = {},
            classes = {},
            classNameExpression = "*github.overengineer*",
            methodNameExpression = "add",
            returnType = Object.class
    )
    public static class TestInterceptor implements Aspect {

        int i = 0;

        @Override
        public Object advise(JoinPointInvocation invocation) throws Throwable {
            System.out.println(this);
            if (i > 0) throw new Assertion();
            i++;
            Object result = invocation.invoke();
            System.out.println(this);
            return result;
        }
    }

    @Pointcut(classes = Aspect.class)
    public static class Metaceptor implements Aspect {

        @Override
        public Object advise(JoinPointInvocation invocation) throws Throwable {
            System.out.println("METACEPTOR, ATTACK!!!!");
            return invocation.invoke();
        }
    }


    int threads = 4;
    long duration = 5000;
    long primingRuns = 10000;

    @Test
    public void testContainerCreationSpeed() throws Exception {

        long mines = new ConcurrentExecutionAssistant.TestThreadGroup(new ConcurrentExecutionAssistant.Execution() {
            @Override
            public void execute() throws HotSwapException {
                final Container container3 = new DefaultContainer(new DefaultComponentStrategyFactory())
                        .add(IBean.class, Bean.class)
                        .add(IBean2.class, Bean2.class);
                container3.get(IBean.class);
            }
        }, threads).run(duration, primingRuns, "my container creation");

        long picos = new ConcurrentExecutionAssistant.TestThreadGroup(new ConcurrentExecutionAssistant.Execution() {
            @Override
            public void execute() throws HotSwapException {
                final PicoContainer picoContainer = new DefaultPicoContainer()
                        .addComponent(IBean.class, Bean.class)
                        .addComponent(IBean2.class, Bean2.class);
                picoContainer.getComponent(IBean.class);
            }
        }, threads).run(duration, primingRuns, "pico container creation");

        long guices = new ConcurrentExecutionAssistant.TestThreadGroup(new ConcurrentExecutionAssistant.Execution() {
            @Override
            public void execute() throws HotSwapException {
                final Injector injector = Guice.createInjector(new AbstractModule() {
                    @Override
                    protected void configure() {
                        bind(IBean.class).to(Bean.class);
                        bind(IBean2.class).to(Bean2.class);
                    }
                });
                injector.getInstance(IBean.class);
            }
        }, threads).run(duration, primingRuns, "guice container creation");

        long springs = new ConcurrentExecutionAssistant.TestThreadGroup(new ConcurrentExecutionAssistant.Execution() {
            @Override
            public void execute() throws HotSwapException {

                final GenericApplicationContext applicationContext = new GenericApplicationContext();

                GenericBeanDefinition beanDefinition2 = new GenericBeanDefinition();
                beanDefinition2.setBeanClass(Bean2.class);
                applicationContext.registerBeanDefinition("bean2", beanDefinition2);

                GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
                beanDefinition.setBeanClass(Bean.class);
                ConstructorArgumentValues constructorArgs = new ConstructorArgumentValues();
                constructorArgs.addIndexedArgumentValue(0, applicationContext.getBean("bean2"));

                beanDefinition.setConstructorArgumentValues(constructorArgs);
                applicationContext.registerBeanDefinition("bean", beanDefinition);

                applicationContext.getBean("bean");

            }
        }, threads).run(duration, primingRuns, "spring container creation");

        printComparison(mines, picos, "pico");
        printComparison(mines, guices, "guice");
        printComparison(mines, springs, "spring");
    }

    @Pointcut(classes = {IBean.class, ISingleton.class})
    public static class TestInterceptor2 implements Aspect {
        @Override
        public Object advise(JoinPointInvocation invocation) throws Throwable {
            return invocation.invoke();
        }
    }

    @Test
    public void testPlainPrototypingSpeed() throws Exception {

        final Container container3 = new DefaultContainer(new DefaultComponentStrategyFactory())
                .add(IBean.class, Bean.class)
                .add(IBean2.class, Bean2.class);

        long mines = new ConcurrentExecutionAssistant.TestThreadGroup(new ConcurrentExecutionAssistant.Execution() {
            @Override
            public void execute() throws HotSwapException {
                container3.get(IBean.class).stuff();
            }
        }, threads).run(duration, primingRuns, "my plain prototype");

        final PicoContainer picoContainer = new DefaultPicoContainer()
                .addComponent(IBean.class, Bean.class)
                .addComponent(IBean2.class, Bean2.class);

        long picos = new ConcurrentExecutionAssistant.TestThreadGroup(new ConcurrentExecutionAssistant.Execution() {
            @Override
            public void execute() throws HotSwapException {
                picoContainer.getComponent(IBean.class).stuff();
            }
        }, threads).run(duration, primingRuns, "pico plain prototype");

        final Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(IBean.class).to(Bean.class);
                bind(IBean2.class).to(Bean2.class);
            }
        });

        long guices = new ConcurrentExecutionAssistant.TestThreadGroup(new ConcurrentExecutionAssistant.Execution() {
            @Override
            public void execute() throws HotSwapException {
                injector.getInstance(IBean.class).stuff();
            }
        }, threads).run(duration, primingRuns, "guice plain prototypes");

        final ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring-plain-prototype.xml");

        long springs = new ConcurrentExecutionAssistant.TestThreadGroup(new ConcurrentExecutionAssistant.Execution() {
            @Override
            public void execute() throws HotSwapException {
                applicationContext.getBean(Bean.class).stuff();
            }
        }, threads).run(duration, primingRuns, "spring plain prototypes");

        printComparison(mines, picos, "pico");
        printComparison(mines, guices, "guice");
        printComparison(mines, springs, "spring");
    }

    private void printComparison(long mine, long theirs, String theirName) {
        System.out.println(mine/(theirs * 1.0d) + " times faster than " + theirName);
    }

    @Test
    public void testSingletonSpeed() throws Exception {

        final Container container2 = ProxyUtil.getRealComponent(new DefaultContainer(new DefaultComponentStrategyFactory())
                .loadModule(new ProxyModule())
                .get(HotSwappableContainer.class)
                .add(ISingleton.class, Singleton.class));

        long mines = new ConcurrentExecutionAssistant.TestThreadGroup(new ConcurrentExecutionAssistant.Execution() {
            @Override
            public void execute() throws HotSwapException {
                container2.get(ISingleton.class).yo();
            }
        }, threads).run(duration, primingRuns, "my singleton");

        final PicoContainer picoContainer3 = new DefaultPicoContainer(new Storing())
                .addComponent(ISingleton.class, Singleton.class);

        long picos = new ConcurrentExecutionAssistant.TestThreadGroup(new ConcurrentExecutionAssistant.Execution() {
            @Override
            public void execute() throws HotSwapException {
                picoContainer3.getComponent(ISingleton.class).yo();
            }
        }, threads).run(duration, primingRuns, "pico singleton");

        final Injector injector3 = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(ISingleton.class).to(Singleton.class).in(Scopes.SINGLETON);
            }
        });

        long guices = new ConcurrentExecutionAssistant.TestThreadGroup(new ConcurrentExecutionAssistant.Execution() {
            @Override
            public void execute() throws HotSwapException {
                injector3.getInstance(ISingleton.class).yo();
            }
        }, threads).run(duration, primingRuns, "guice singleton");

        final ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring-singleton.xml");

        long springs = new ConcurrentExecutionAssistant.TestThreadGroup(new ConcurrentExecutionAssistant.Execution() {
            @Override
            public void execute() throws HotSwapException {
                applicationContext.getBean(Singleton.class).yo();
            }
        }, threads).run(duration, primingRuns, "spring singleton");

        printComparison(mines, picos, "pico");
        printComparison(mines, guices, "guice");
        printComparison(mines, springs, "spring");

    }

    @Test
    public void testCyclicRefSpeed() throws Exception {

        final Container container = new DefaultContainer(new DefaultComponentStrategyFactory())
                .loadModule(new ProxyModule())
                .get(HotSwappableContainer.class)
                .add(ICyclicRef.class, PCyclicTest.class)
                .add(ICyclicRef2.class, CyclicTest2.class)
                .add(ICyclicRef3.class, CyclicTest3.class);

        long mines= new ConcurrentExecutionAssistant.TestThreadGroup(new ConcurrentExecutionAssistant.Execution() {
            @Override
            public void execute() throws HotSwapException {
                container.get(ICyclicRef2.class);
            }
        }, threads).run(duration, primingRuns, "my cyclic refs");

        final Injector injector2 = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(ICyclicRef.class).to(PCyclicTest.class);
                bind(ICyclicRef2.class).to(CyclicTest2.class);
                bind(ICyclicRef3.class).to(CyclicTest3.class);
            }
        });

        long guices = new ConcurrentExecutionAssistant.TestThreadGroup(new ConcurrentExecutionAssistant.Execution() {
            @Override
            public void execute() throws HotSwapException {
                injector2.getInstance(ICyclicRef2.class);
            }
        }, threads).run(duration, primingRuns, "guice cyclic refs");

        printComparison(mines, guices, "guice");

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

    public interface ISingleton{void yo();}

    public static class Singleton implements ISingleton {
        @Override
        public void yo() {
        }
    }

    public static class Assertion extends RuntimeException {}

}
