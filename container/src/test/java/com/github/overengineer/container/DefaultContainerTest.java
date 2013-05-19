package com.github.overengineer.container;

import com.github.overengineer.container.key.SerializableKey;
import com.github.overengineer.container.metadata.Component;
import com.github.overengineer.container.metadata.Prototype;
import com.github.overengineer.container.proxy.HotSwapException;
import com.github.overengineer.container.proxy.HotSwappableContainer;
import com.github.overengineer.container.proxy.ProxyModule;
import com.github.overengineer.container.proxy.aop.*;
import com.github.overengineer.container.key.GenericKey;
import com.google.inject.*;
import com.google.inject.Injector;
import org.junit.Test;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoBuilder;
import org.picocontainer.PicoContainer;
import org.picocontainer.behaviors.Storing;
import org.picocontainer.containers.TransientPicoContainer;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import scope.CommonConstants;
import scope.CommonModule;
import scope.monitor.DefaultSchedulerProvider;
import scope.monitor.ScheduledExecutorTimeoutMonitor;
import scope.monitor.SchedulerProvider;
import scope.monitor.TimeoutMonitor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

/**
 *
 */
public class DefaultContainerTest implements Serializable {

    @Test
    public void testLoadModule() {

        Container container = Clarence.please().gimmeThatTainer();

        container.loadModule(CommonModule.class);

        TimeoutMonitor monitor = container.get(TimeoutMonitor.class);

        assertNotNull(monitor);

    }

    @Test
    public void testSerialization() {

        AopContainer container = Clarence.please().gimmeThatAopTainer();

        container.add(IBean.class, Bean3.class);

        container.add(IBean2.class, Bean2.class);

        container.addInstance(new GenericKey<List<Integer>>() {}, new ArrayList<Integer>());

        container.loadModule(CommonModule.class);

        container.addAspect(TestAspect.class)
                .addAspect(Metaceptor.class);

        List<String> strings = new ArrayList<String>();

        container.addInstance(new GenericKey<List<String>>() {}, strings);

        container = SerializationTestingUtil.getSerializedCopy(container);

        TimeoutMonitor monitor = container.get(TimeoutMonitor.class);

        assertNotNull(monitor);

        assertEquals(strings, container.get(new GenericKey<List<String>>(){}));

        assertNotNull(container.get(IBean.class));
    }

    @Test
    public void testVerify_positive() throws WiringException {

        Container container = Clarence.please().gimmeThatTainer();

        container.verify();

        container.loadModule(CommonModule.class);

        container.verify();

    }

    @Test(expected = WiringException.class)
    public void testVerify_negative() throws WiringException {

        Container container = Clarence.please().gimmeThatTainer();

        container.add(TimeoutMonitor.class, ScheduledExecutorTimeoutMonitor.class);

        container.addProperty(CommonConstants.Properties.MONITORING_FREQUENCY, 4L);

        container.verify();

    }

    @Test
    public void testAddChild() {

        Container master = Clarence.please().gimmeThatTainer();

        Container common = Clarence.please().gimmeThatTainer();

        Container sibling = Clarence.please().gimmeThatTainer();

        common.loadModule(CommonModule.class);

        master.addChild(common);

        master.addChild(sibling);

        assertNotNull(common.get(TimeoutMonitor.class));

        assertNotNull(master.get(TimeoutMonitor.class));

        try {
            assertNull(sibling.get(TimeoutMonitor.class));
        } catch (MissingDependencyException e) {
             //sup
        }

        Container cascadeFuck = Clarence.please().gimmeThatTainer();

        master.addCascadingContainer(cascadeFuck);

        try {
            cascadeFuck.addChild(common);
        } catch (CircularReferenceException e) {

        }

        cascadeFuck.loadModule(CommonModule.class);

        assertNotNull(sibling.get(TimeoutMonitor.class));

        try {
            common.get(TestInterceptor2.class);
        } catch (MissingDependencyException e) {
            //sup
        }

        Container global = Clarence.please().gimmeThatTainer();

        global.add(ISingleton.class, Singleton.class);

        master.addCascadingContainer(global);

        sibling.get(ISingleton.class);

        try {
            master.get(TestInterceptor2.class);
        } catch (MissingDependencyException e) {
            //sup
        }

    }

    @Pointcut(classes = {IBean.class, ISingleton.class})
    public static class TestInterceptor2 implements Aspect {
        @Override
        public Object advise(JoinPointInvocation invocation) throws Throwable {
            return invocation.invoke();
        }
    }

    @Test
    public void testAddAndGetComponent() {

        Container container = Clarence.please().gimmeThatTainer();

        container.add(SchedulerProvider.class, DefaultSchedulerProvider.class);

        container.addProperty(CommonConstants.Properties.MONITORING_THREAD_POOL_SIZE, 4);

        SchedulerProvider provider = container.get(SchedulerProvider.class);

        assertTrue(provider instanceof DefaultSchedulerProvider);

        container.add(IConstructorTest.class, ConstructorTest.class);

        assertEquals(provider, ((ConstructorTest) container.get(IConstructorTest.class)).provider);

    }

    @Test
    public void testAddAndGetInstance() {

        Container container = Clarence.please().gimmeThatTainer();

        SchedulerProvider given = new DefaultSchedulerProvider();

        container.addInstance(SchedulerProvider.class, given);

        container.addProperty(CommonConstants.Properties.MONITORING_THREAD_POOL_SIZE, 4);

        SchedulerProvider received = container.get(SchedulerProvider.class);

        assertEquals(given, received);

    }

    @Test
    public void testAddAndGetProperty() {

        Container container = Clarence.please().gimmeThatAopTainer();

        container.addProperty("test", 69L);

        assertEquals((Long) 69L, container.getProperty(Long.TYPE, "test"));

        container.get(ComponentStrategyFactory.class);

    }

    @Test
    public void testAddAndGetGeneric() {

        Container container = Clarence.please().gimmeThatTainer();

        List<String> strings = new ArrayList<String>();

        List<Integer> integers = new ArrayList<Integer>();

        container.addInstance(new GenericKey<List<String>>(){}, strings);

        container.addInstance(new GenericKey<List<Integer>>(){}, integers);

        assertEquals(strings, container.get(new GenericKey<List<String>>() {
        }));

        assertEquals(integers, container.get(new GenericKey<List<Integer>>(){}));

    }

    @Test
    public void testRegisterFactory() {

        Container container = Clarence.please().gimmeThatTainer();

        container.loadModule(CommonModule.class);

        container.registerManagedComponentFactory(new GenericKey<Factory<TimeoutMonitor>>() {});

        Factory<TimeoutMonitor> timeoutMonitorFactory = container.get(new GenericKey<Factory<TimeoutMonitor>>(){});

        assert timeoutMonitorFactory.create() != null;

        container.add(IConstructorTest.class, FactoryTest.class);

        IConstructorTest i = container.get(IConstructorTest.class);

        assertEquals(timeoutMonitorFactory, ((FactoryTest) i).timeoutMonitorFactory);

        System.out.println(timeoutMonitorFactory);

        container.add(new GenericKey<Factory<TimeoutMonitor>>(){}, FactoryTest.class);

        container = SerializationTestingUtil.getSerializedCopy(container);

        timeoutMonitorFactory = container.get(new GenericKey<Factory<TimeoutMonitor>>(){});

        assert timeoutMonitorFactory.create() != null;

        assert timeoutMonitorFactory instanceof FactoryTest;

    }

    @Test
    public void testNonManagedComponentFactory() {

        Container container = Clarence.please().gimmeThatTainer();

        SerializableKey factoryKey = new GenericKey<NonManagedComponentFactory<NamedComponent>>() {};

        container.registerNonManagedComponentFactory(factoryKey, NonManagedComponent.class);

        container.loadModule(CommonModule.class);

        container = SerializationTestingUtil.getSerializedCopy(container);

        NonManagedComponentFactory<NamedComponent> namedComponentFactory = container.get(factoryKey);

        assertEquals("test", namedComponentFactory.create("test").getName());

    }

    public interface NonManagedComponentFactory<T extends NamedComponent> {
        T create(String name);
    }

    public interface NamedComponent {
        String getName();
    }

    public static class NonManagedComponent implements NamedComponent {
        String name;
        public NonManagedComponent(TimeoutMonitor timeoutMonitor, String name) {
            this.name = name;
        }
        @Override
        public String getName() {
            return name;
        }
    }

    public static interface Factory<T>{
        T create();
    }

    public static class FactoryTest implements IConstructorTest, Factory, Serializable {

        Factory<TimeoutMonitor> timeoutMonitorFactory;

        public FactoryTest(Factory<TimeoutMonitor> timeoutMonitorFactory) {
            this.timeoutMonitorFactory = timeoutMonitorFactory;
        }

        @Override
        public Object create() {
            return timeoutMonitorFactory.create();
        }
    }


    @Test(expected = UnsupportedOperationException.class)
    public void testLifecycle() {
        Clarence.please().makeYourStuffInjectable().gimmeThatTainer().makeInjectable()
                .loadModule(CommonModule.class)
                .add(LifecycleControl.class, DefaultLifecycleControl.class)
                .get(LifecycleControl.class)
                .start();
    }


    @Test(expected = Assertion.class)
    public void testAddListener() {

        Container container = Clarence.please().gimmeThatTainer().addListener(Listener.class)
                .add(SchedulerProvider.class, DefaultSchedulerProvider.class)
                .addProperty(CommonConstants.Properties.MONITORING_THREAD_POOL_SIZE, 4);

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

        HotSwappableContainer container = Clarence.please().gimmeThatProxyTainer();

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

        HotSwappableContainer container = Clarence.please().gimmeThatProxyTainer();

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

        HotSwappableContainer container = Clarence.please().gimmeThatProxyTainer();

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

        AopContainer c = Clarence.please().gimmeThatAopTainer().makeInjectable().get(AopContainer.class);

        c.loadModule(CommonModule.class);

        c.addAspect(TestAspect.class).addAspect(Metaceptor.class);
        c.addInstance(SchedulerProvider.class, new DefaultSchedulerProvider());
        c.add(ICyclicRef3.class, CyclicTest3.class);
    }

    @Pointcut(
            paramterTypes = {Class.class, Object.class},
            annotations = {},
            classes = {},
            classNameExpression = "*github.overengineer*",
            methodNameExpression = "add*",
            returnType = Object.class
    )
    public static class TestAspect implements Aspect {

        public TestAspect(TimeoutMonitor monitor) {

        }

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



    @Test
    public void testNewEmptyClone() {
        final Container container = Clarence.please().makeYourStuffInjectable().gimmeThatTainer()
                .addListener(L.class);

        assert container.newEmptyClone().getAllComponents().size() == 1;
    }

    public static class L implements ComponentInitializationListener {
        @Override
        public <T> T onInitialization(T component) {
            return component;
        }
    }

    @Test
    public void testAddCustomProvider() {
        final Container container = Clarence.please().gimmeThatTainer()
                .addCustomProvider(ProvidedType.class, Provider.class);

        assert container.get(ProvidedType.class) != null;
    }

    public static class Provider {
        ProvidedType get(Provider provider) {
            System.out.println("i got myself");
            return new ProvidedType();
        }
    }

    public static class ProvidedType {

    }


    int threads = 4;
    long duration = 5000;
    long primingRuns = 10000;

    private void printComparison(long mine, long theirs, String theirName) {
        System.out.println(mine/(theirs * 1.0d) + " times faster than " + theirName);
    }

    @Test
    public void testContainerCreationSpeed() throws Exception {

        long mines = new ConcurrentExecutionAssistant.TestThreadGroup(new ConcurrentExecutionAssistant.Execution() {
            @Override
            public void execute() throws HotSwapException {
                Clarence.please().gimmeThatTainer()
                        .add(IBean.class, Bean.class)
                        .add(IBean2.class, Bean2.class)
                        .get(IBean.class);
            }
        }, threads).run(duration, primingRuns, "my container creation");

        long picos = new ConcurrentExecutionAssistant.TestThreadGroup(new ConcurrentExecutionAssistant.Execution() {
            @Override
            public void execute() throws HotSwapException {
                new TransientPicoContainer()
                        .addComponent(IBean.class, Bean.class)
                        .addComponent(IBean2.class, Bean2.class)
                        .getComponent(IBean.class);
            }
        }, threads).run(duration, primingRuns, "pico container creation");

        long guices = new ConcurrentExecutionAssistant.TestThreadGroup(new ConcurrentExecutionAssistant.Execution() {
            @Override
            public void execute() throws HotSwapException {
                Guice.createInjector(new AbstractModule() {
                    @Override
                    protected void configure() {
                        bind(IBean.class).to(Bean.class);
                        bind(IBean2.class).to(Bean2.class);
                    }
                }).getInstance(IBean.class);
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

    @Test
    public void testPlainPrototypingSpeed() throws Exception {

        final Container container3 = Clarence.please().gimmeThatTainer()
                .add(IBean.class, Bean.class)
                .add(IBean2.class, Bean2.class);

        long mines = new ConcurrentExecutionAssistant.TestThreadGroup(new ConcurrentExecutionAssistant.Execution() {
            @Override
            public void execute() throws HotSwapException {
                container3.get(IBean.class).stuff();
            }
        }, threads).run(duration, primingRuns, "my plain prototype");

        final MutablePicoContainer picoContainer = new DefaultPicoContainer()
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

    @Test
    public void testSingletonSpeed() throws Exception {

        final Container container2 = Clarence.please().gimmeThatTainer()
                .add(ISingleton.class, Singleton.class)
                .getReal();

        long mines = new ConcurrentExecutionAssistant.TestThreadGroup(new ConcurrentExecutionAssistant.Execution() {
            @Override
            public void execute() throws HotSwapException {
                container2.get(ISingleton.class).yo();
            }
        }, threads).run(duration, primingRuns, "my singleton");

        final PicoContainer picoContainer3 = new TransientPicoContainer()
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

        final Container container = Clarence.please().gimmeThatProxyTainer()
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
