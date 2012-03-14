package org.byars.struts2.actions.selenium;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.selenium.Selenium;

/**
 * @author rees.byars
 */
public class DefaultSeleniumThread extends AbstractEasyThread implements
        SeleniumThread {

    private Logger log = LoggerFactory.getLogger(DefaultSeleniumThread.class);
    private Selenium selenium;
    private List<SeleniumOperation> seleniumOperations = new ArrayList<SeleniumOperation>();

    @Override
    public void setSelenium(Selenium selenium) {
        this.selenium = selenium;
    }

    @Override
    public void addOperation(SeleniumOperation operation) {
        this.seleniumOperations.add(operation);
    }

    @Override
    public synchronized void start() {
        super.start();
        try {
            selenium.start();
            log.debug("Selenium successfully started!");
        } catch (Exception se) {
            log.debug("Selenium was already running!");
        }
    }

    @Override
    public synchronized void stop() {
        super.stop();
        try {
            selenium.stop();
            log.debug("Selenium successfully stopped!");
        } catch (Exception se) {
            log.debug("Selenium was already stopped!");
        }
    }

    @Override
    public void doWhileRunning() {
        for (SeleniumOperation operation : this.seleniumOperations) {
            operation.doOperation(selenium);
        }
    }

}
