package scope;

import com.github.overengineer.container.key.GenericKey;
import com.github.overengineer.container.module.BaseModule;
import scope.monitor.*;

import java.util.Collections;
import java.util.Set;

/**
 *
 */
public class CommonModule extends BaseModule {

    @Override
    protected void configure() {

        use(EmptyActionProvider.class).forType(ActionProvider.class);

        use(new GenericKey<ScheduledExecutorTimeoutMonitor<FakeTimeoutable>>(){})
                .forType(TimeoutMonitor.class)
                .forType(new GenericKey<TimeoutMonitor<FakeTimeoutable>>() {});

        use(DefaultSchedulerProvider.class).forType(SchedulerProvider.class);

        use(CommonConstants.Defaults.MONITORING_FREQUENCY).withName(CommonConstants.Properties.MONITORING_FREQUENCY);

        use(CommonConstants.Defaults.MONITORING_THREAD_POOL_SIZE).withName(CommonConstants.Properties.MONITORING_THREAD_POOL_SIZE);
    }

    public static class FakeTimeoutable implements Timeoutable<FakeTimeoutable> {

        @Override
        public void setMaxIdleTime(long maxIdleTime) {
        }

        @Override
        public String getId() {
            return null;
        }

        @Override
        public long getRemainingTime() {
            return 0;
        }

        @Override
        public void timeout() {
        }

        @Override
        public void reset() {
        }

        @Override
        public void addTimeoutListener(TimeoutListener timeoutListener) {
        }
    }

    public static class EmptyActionProvider implements ActionProvider {

        @Override
        public Set<Class<?>> getActionClasses() {
            return Collections.emptySet();
        }

    }
    
}
