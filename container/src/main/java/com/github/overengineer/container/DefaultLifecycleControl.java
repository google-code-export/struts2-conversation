package com.github.overengineer.container;

import com.github.overengineer.container.metadata.MetadataAdapter;

/**
 * @author rees.byars
 */
public class DefaultLifecycleControl implements LifecycleControl {

    private final Container container;
    private final MetadataAdapter metadataAdapter;

    public DefaultLifecycleControl(Container container, MetadataAdapter metadataAdapter) {
        this.container = container;
        this.metadataAdapter = metadataAdapter;
    }

    @Override
    public void start() {
        for (Object component : container.getAllComponents()) {
            metadataAdapter.notifyStartedIfEligible(component);
        }
    }

    @Override
    public void stop() {
        for (Object component : container.getAllComponents()) {
            metadataAdapter.notifyStoppedIfEligible(component);
        }
    }

}
