package scope.monitor;

import java.io.Serializable;
import java.util.concurrent.ScheduledExecutorService;

public interface SchedulerProvider extends Serializable {

    ScheduledExecutorService getScheduler();

}
