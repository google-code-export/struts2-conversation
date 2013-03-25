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
 */
public class SimpleScopeContainerTest {

    @Test
    public void testLoadModule() {

        StandaloneContainer container = new SimpleScopeContainer();

        container.loadModule(new CommonModule());

        TimeoutMonitor monitor = container.getComponent(TimeoutMonitor.class);

        assertNotNull(monitor);

    }

    @Test
    public void testVerify_positive() throws WiringException {

        StandaloneContainer container = new SimpleScopeContainer();

        container.verify();

        container.loadModule(new CommonModule());

        container.verify();

    }

    @Test(expected = WiringException.class)
    public void testVerify_negative() throws WiringException {

        StandaloneContainer container = new SimpleScopeContainer();

        container.add(TimeoutMonitor.class, ScheduledExecutorTimeoutMonitor.class);

        container.verify();

    }

    @Test
    public void testAddAndGetComponent() {

        StandaloneContainer container = new SimpleScopeContainer();

        container.add(SchedulerProvider.class, DefaultSchedulerProvider.class);

        SchedulerProvider provider = container.getComponent(SchedulerProvider.class);

        assertTrue(provider instanceof DefaultSchedulerProvider);

    }

    @Test
    public void testAddAndGetInstance() {

        StandaloneContainer container = new SimpleScopeContainer();

        SchedulerProvider given = new DefaultSchedulerProvider();

        container.addInstance(SchedulerProvider.class, given);

        SchedulerProvider received = container.getComponent(SchedulerProvider.class);

        assertEquals(given, received);

    }

    @Test
    public void testAddAndGetProperty() {

        StandaloneContainer container = new SimpleScopeContainer();

        container.addProperty("test", 69L);

        assertEquals((Long) 69L, container.getProperty(Long.TYPE, "test"));

    }

}
