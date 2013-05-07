package scope;

import com.github.overengineer.container.BaseModule;
import scope.monitor.DefaultSchedulerProvider;
import scope.monitor.ScheduledExecutorTimeoutMonitor;
import scope.monitor.SchedulerProvider;
import scope.monitor.TimeoutMonitor;

import java.util.Collections;
import java.util.Set;

/**
 *
 */
public class CommonModule extends BaseModule {
    
    public CommonModule() {

        use(EmptyActionProvider.class).forType(ActionProvider.class);

        use(ScheduledExecutorTimeoutMonitor.class).forType(TimeoutMonitor.class);

        use(DefaultSchedulerProvider.class).forType(SchedulerProvider.class);

        set(CommonConstants.Properties.MONITORING_FREQUENCY)
                .to(CommonConstants.Defaults.MONITORING_FREQUENCY);

        set(CommonConstants.Properties.MONITORING_THREAD_POOL_SIZE)
                .to(CommonConstants.Defaults.MONITORING_THREAD_POOL_SIZE);
    }

    public static class EmptyActionProvider implements ActionProvider {

        @Override
        public Set<Class<?>> getActionClasses() {
            return Collections.emptySet();
        }

    }
    
}
