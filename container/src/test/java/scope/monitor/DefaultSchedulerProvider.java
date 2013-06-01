package scope.monitor;

import com.github.overengineer.container.metadata.Inject;
import com.github.overengineer.container.metadata.Named;
import com.github.overengineer.container.metadata.PostConstructable;
import scope.CommonConstants;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DefaultSchedulerProvider implements SchedulerProvider, PostConstructable {

    private static final long serialVersionUID = -6832427883773358141L;

    protected int monitoringThreadPoolSize;
    protected transient ScheduledExecutorService scheduler;
    protected Lock schedulerGuard = new ReentrantLock();

    @Inject
    public void setMonitoringThreadPoolSize(@Named(CommonConstants.Properties.MONITORING_THREAD_POOL_SIZE) Integer monitoringThreadPoolSize) {
        this.monitoringThreadPoolSize = monitoringThreadPoolSize;
    }

    @Override
    public void init() {
    }

    @Override
    public ScheduledExecutorService getScheduler() {
        if (scheduler != null) {
            return scheduler;
        }
        schedulerGuard.lock();
        try {
            if (scheduler != null) {
                return scheduler;
            }
            init();
            return scheduler;
        } finally {
            schedulerGuard.unlock();
        }
    }

}
