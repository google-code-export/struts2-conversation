package org.byars.struts2.actions.selenium;

public class DefaultSeleniumThreadFactory implements SeleniumThreadFactory {

    protected SeleniumFactory seleniumFactory;

    @Override
    public void setSeleniumFactory(SeleniumFactory seleniumFactory) {
        this.seleniumFactory = seleniumFactory;
    }

    @Override
    public SeleniumThread createThread() {
        SeleniumThread seleniumThread = new DefaultSeleniumThread();
        seleniumThread.setSelenium(this.seleniumFactory.createSelenium());
        return seleniumThread;
    }

}
