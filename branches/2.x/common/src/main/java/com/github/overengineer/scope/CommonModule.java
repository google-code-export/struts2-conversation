package com.github.overengineer.scope;

import com.github.overengineer.scope.bijection.BijectorImplFactory;
import com.github.overengineer.container.module.BaseModule;
import com.github.overengineer.scope.monitor.DefaultSchedulerProvider;
import com.github.overengineer.scope.monitor.SchedulerProvider;
import com.github.overengineer.scope.bijection.BijectorFactory;

import java.util.Collections;
import java.util.Set;

/**
 *
 */
public class CommonModule extends BaseModule {

    @Override
    protected void configure() {

        use(EmptyActionProvider.class).forType(ActionProvider.class);

        use(DefaultSchedulerProvider.class).forType(SchedulerProvider.class);

        use(BijectorImplFactory.class).forType(BijectorFactory.class);

        use(CommonConstants.Defaults.MONITORING_THREAD_POOL_SIZE).withQualifier(CommonConstants.Properties.MONITORING_THREAD_POOL_SIZE);
    }

    public static class EmptyActionProvider implements ActionProvider {

        @Override
        public Set<Class<?>> getActionClasses() {
            return Collections.emptySet();
        }

    }
    
}
