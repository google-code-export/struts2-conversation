package org.byars.struts2.actions.selenium;

import com.thoughtworks.selenium.Selenium;

public class LeakOperation implements SeleniumOperation {

    @Override
    public void doOperation(Selenium selenium) {
        selenium.open("/conversation-example/struts2/booking/begin-booking.action");
    }

}
