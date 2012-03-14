package org.byars.struts2.actions.selenium;

import java.util.List;

public abstract class AbstractSeleniumBattery implements SeleniumBattery {

    protected SeleniumFactory seleniumFactory;
    protected SeleniumThreadFactory seleniumThreadFactory;

    @Override
    public void setSeleniumFactory(SeleniumFactory seleniumFactory) {
        this.seleniumFactory = seleniumFactory;
    }

    @Override
    public void setSeleniumThreadFactory(
            SeleniumThreadFactory seleniumThreadFactory) {
        this.seleniumThreadFactory = seleniumThreadFactory;
    }

    @Override
    public void executeModules() throws InterruptedException {
        this.seleniumThreadFactory.setSeleniumFactory(this.seleniumFactory);
        for (SeleniumModule module : this.configuration()) {
            module.setSeleniumThreadFactory(this.seleniumThreadFactory);
            module.execute();
        }
    }

    public abstract List<SeleniumModule> configuration();

}
