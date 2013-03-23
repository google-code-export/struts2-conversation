package com.github.overengineer.scope;

import com.github.overengineer.scope.container.BaseModule;
import com.github.overengineer.scope.monitor.DefaultSchedulerProvider;
import com.github.overengineer.scope.monitor.ScheduledExecutorTimeoutMonitor;
import com.github.overengineer.scope.monitor.SchedulerProvider;
import com.github.overengineer.scope.monitor.TimeoutMonitor;
import com.github.overengineer.scope.util.BijectorFactory;

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

        use(BijectorFactory.class).forType(BijectorFactory.class);

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
