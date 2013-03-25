package com.github.overengineer.scope.container.standalone;

import com.github.overengineer.scope.CommonModule;
import com.github.overengineer.scope.container.ScopeContainer;
import com.github.overengineer.scope.monitor.ScheduledExecutorTimeoutMonitor;
import com.github.overengineer.scope.monitor.TimeoutMonitor;
import org.junit.Test;

/**
 */
public class SimpleScopeContainerTest {


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

}
