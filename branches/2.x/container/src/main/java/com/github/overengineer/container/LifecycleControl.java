package com.github.overengineer.container;

import java.io.Serializable;

/**
 * @author rees.byars
 */
public interface LifecycleControl extends Serializable {
    void start();
    void stop();
}
