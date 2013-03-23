package com.github.overengineer.scope.monitor;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.github.overengineer.scope.testutil.SerializationTestingUtil;

public class DefaultSchedulerProviderTest {

    @Test
    public void testSerialization() {
        DefaultSchedulerProvider provider = new DefaultSchedulerProvider();
        provider.setMonitoringThreadPoolSize(3);
        provider.init();

        assertNotNull(provider.scheduler);

        provider = SerializationTestingUtil.getSerializedCopy(provider);

        assertNull(provider.scheduler);

        provider.init();

        assertNotNull(provider.scheduler);
    }

}
