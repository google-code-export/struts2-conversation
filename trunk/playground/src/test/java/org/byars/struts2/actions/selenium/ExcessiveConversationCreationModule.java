package org.byars.struts2.actions.selenium;

import java.util.HashSet;
import java.util.Set;

public class ExcessiveConversationCreationModule implements SeleniumModule {

    protected SeleniumThreadFactory seleniumThreadFactory;

    public void setSeleniumThreadFactory(
            SeleniumThreadFactory seleniumThreadFactory) {
        this.seleniumThreadFactory = seleniumThreadFactory;
    }

    @Override
    public void execute() throws InterruptedException {

        Set<SeleniumThread> seleniumThreads = new HashSet<SeleniumThread>();
        SeleniumOperation leakOperation = new LeakOperation();

        for (int i = 0; i < 5; i++) {
            SeleniumThread seleniumThread = seleniumThreadFactory
                    .createThread();
            seleniumThread.addOperation(leakOperation);
            seleniumThreads.add(seleniumThread);
        }

        int i = 0;
        for (SeleniumThread seleniumThread : seleniumThreads) {
            System.out.println("Starting Selenium instance #" + ++i);
            seleniumThread.start();

        }

        long minute = 60000L;
        Thread.sleep(minute * 20);

        i = 0;
        for (SeleniumThread seleniumThread : seleniumThreads) {
            System.out.println("Stopping Selenium instance #" + ++i);
            seleniumThread.stop();
        }
    }

}
