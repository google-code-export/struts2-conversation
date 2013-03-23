package com.github.overengineer.scope.monitor;

import static org.junit.Assert.assertNotNull;

import java.io.Serializable;

import org.junit.Test;

import com.github.overengineer.scope.testutil.SerializationTestingUtil;

public class ScheduledExecutorTimeoutMonitorTest implements Serializable {

    private static final long serialVersionUID = 1L;

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Test
    public void testSerialization() throws InterruptedException {

        DefaultSchedulerProvider provider = new DefaultSchedulerProvider();
        provider.setMonitoringThreadPoolSize(3);
        provider.init();

        ScheduledExecutorTimeoutMonitor monitor = new ScheduledExecutorTimeoutMonitor();
        monitor.setMonitoringFrequency(1000L);
        monitor.setSchedulerProvider(provider);
        monitor.init();
        monitor.addTimeoutable(new Timeoutable() {

            private static final long serialVersionUID = 1L;

            @Override
            public void setMaxIdleTime(long maxIdleTime) {
            }

            @Override
            public String getId() {
                return "sup";
            }

            @Override
            public long getRemainingTime() {
                return 0;
            }

            @Override
            public void timeout() {
                System.out.println("asdfasdf");
            }

            @Override
            public void reset() {
            }

            @Override
            public void addTimeoutListener(TimeoutListener timeoutListener) {
            }

        });

        assertNotNull(monitor.scheduler);

        monitor = SerializationTestingUtil.getSerializedCopy(monitor);

        assertNotNull(monitor.scheduler);

        //Thread.sleep(2000L);
    }

}
