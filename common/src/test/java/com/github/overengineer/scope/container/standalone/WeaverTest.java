package com.github.overengineer.scope.container.standalone;

import com.github.overengineer.scope.CommonModule;
import com.github.overengineer.scope.monitor.DefaultSchedulerProvider;
import com.github.overengineer.scope.monitor.ScheduledExecutorTimeoutMonitor;
import com.github.overengineer.scope.monitor.SchedulerProvider;
import com.github.overengineer.scope.monitor.TimeoutMonitor;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 *
 */
public class WeaverTest {

    @Test
    public void testLoadModule() {

        Container container = new Weaver();

        container.loadModule(new CommonModule());

        TimeoutMonitor monitor = container.get(TimeoutMonitor.class);

        assertNotNull(monitor);

    }

    @Test
    public void testVerify_positive() throws WiringException {

        Container container = new Weaver();

        container.verify();

        container.loadModule(new CommonModule());

        container.verify();

    }

    @Test(expected = WiringException.class)
    public void testVerify_negative() throws WiringException {

        Container container = new Weaver();

        container.add(TimeoutMonitor.class, ScheduledExecutorTimeoutMonitor.class);

        container.verify();

    }

    @Test
    public void testAddAndGetComponent() {

        Container container = new Weaver();

        container.add(SchedulerProvider.class, DefaultSchedulerProvider.class);

        SchedulerProvider provider = container.get(SchedulerProvider.class);

        assertTrue(provider instanceof DefaultSchedulerProvider);

        container.add(ConstructorTest.class, ConstructorTest.class);

        assertEquals(provider, container.get(ConstructorTest.class).provider);

    }

    @Test
    public void testAddAndGetInstance() {

        Container container = new Weaver();

        SchedulerProvider given = new DefaultSchedulerProvider();

        container.addInstance(SchedulerProvider.class, given);

        SchedulerProvider received = container.get(SchedulerProvider.class);

        assertEquals(given, received);

    }

    @Test
    public void testAddAndGetProperty() {

        Container container = new Weaver();

        container.addProperty("test", 69L);

        assertEquals((Long) 69L, container.getProperty(Long.TYPE, "test"));

    }

    public static class ConstructorTest {
        SchedulerProvider provider;
        public ConstructorTest(SchedulerProvider provider) {
            this.provider = provider;
        }
    }

}
