package org.byars.struts2.actions;

import java.net.URL;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverBackedSelenium;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import com.thoughtworks.selenium.SeleneseTestCase;

public class LeakTest extends SeleneseTestCase {

    @Before
    public void setUp() throws Exception {
        DesiredCapabilities capability = DesiredCapabilities.firefox();
        WebDriver driver = new RemoteWebDriver(new URL(
                "http://localhost:4444/wd/hub"), capability);
        String baseUrl = "http://localhost:8080/";
        selenium = new WebDriverBackedSelenium(driver, baseUrl);
    }

    @Test
    public void testBookingController() throws Exception {
        for (int i = 0; i < 400; i++) {
            selenium.open("/conversation-example/struts2/booking/begin-booking.action");
        }
    }

    @After
    public void tearDown() throws Exception {
        selenium.stop();
    }

}
