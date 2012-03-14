package org.byars.struts2.actions.selenium;

public interface SeleniumModule {

    public void setSeleniumThreadFactory(
            SeleniumThreadFactory seleniumThreadFactory);

    public void execute() throws InterruptedException;

}
