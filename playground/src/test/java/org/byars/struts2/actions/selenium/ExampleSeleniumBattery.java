package org.byars.struts2.actions.selenium;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExampleSeleniumBattery extends AbstractSeleniumBattery {

    public ExampleSeleniumBattery() {

        SeleniumFactory seleniumFactory = new GridRemoteWebDriverSeleniumFactory();
        seleniumFactory.setBaseUrl("http://localhost:8080/");
        this.setSeleniumFactory(seleniumFactory);

        SeleniumThreadFactory seleniumThreadFactory = new DefaultSeleniumThreadFactory();
        seleniumThreadFactory.setSeleniumFactory(seleniumFactory);
        this.setSeleniumThreadFactory(seleniumThreadFactory);

    }

    @Override
    public List<SeleniumModule> configuration() {

        List<SeleniumModule> modules = new ArrayList<SeleniumModule>();
        modules.add(new ExcessiveConversationCreationModule());

        return modules;
    }

    public static void main(String[] args) throws IOException,
            InterruptedException {

        SeleniumBattery exampleBattery = new ExampleSeleniumBattery();
        exampleBattery.executeModules();

    }

}
