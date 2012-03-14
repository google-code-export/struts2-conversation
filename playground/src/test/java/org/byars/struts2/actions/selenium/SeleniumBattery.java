package org.byars.struts2.actions.selenium;

public interface SeleniumBattery {

    public void setSeleniumFactory(SeleniumFactory seleniumFactory);

    public void setSeleniumThreadFactory(
            SeleniumThreadFactory seleniumThreadFactory);

    public void executeModules() throws InterruptedException;

}
