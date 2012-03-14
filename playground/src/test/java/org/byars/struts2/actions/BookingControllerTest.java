package org.byars.struts2.actions;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.SeleneseTestCase;

public class BookingControllerTest extends SeleneseTestCase {
    @Before
    public void setUp() throws Exception {
        selenium = new DefaultSelenium("localhost", 4444, "*chrome",
                "http://localhost:8080/");
        selenium.start();
    }

    @Test
    public void testBookingController() throws Exception {
        for (int i = 0; i < 200; i++) {
            selenium.open("/conversation-example/struts2/begin-booking.action");
        }

        selenium.type("id=continue-to-room-selection_booking_itemName",
                "sdfasdf");
        selenium.type("id=continue-to-room-selection_booking_itemPrice", "4345");
        selenium.click("id=continue-to-room-selection_0");
        selenium.waitForPageToLoad("30000");
        selenium.click("id=continue-to-amenity-selection_0");
        selenium.waitForPageToLoad("30000");
    }

    @Ignore
    @Test
    public void testBookingControllerWithAjax() throws Exception {
        selenium.open("/conversation-example/pre-booking.action");
        selenium.click("id=begin-booking_0");
        selenium.waitForPageToLoad("30000");
        selenium.type("id=continue-to-room-selection_booking_itemName",
                "sdfgsdf");
        selenium.type("id=continue-to-room-selection_booking_itemPrice", "33");
        selenium.click("id=continue-to-room-selection_0");
        selenium.waitForPageToLoad("30000");
        selenium.click("id=continue-to-amenity-selection_0");
        selenium.waitForPageToLoad("30000");
        selenium.click("id=begin-checkout_0");
        selenium.waitForPageToLoad("30000");
        selenium.click("id=begin-booking_0");
        selenium.waitForPageToLoad("30000");
        selenium.type("id=continue-to-room-selection_booking_itemName",
                "werwerwer");
        selenium.type("id=continue-to-room-selection_booking_itemPrice", "444");
        selenium.click("id=continue-to-room-selection_0");
        selenium.waitForPageToLoad("30000");
        selenium.click("id=begin-booking_0");
        selenium.waitForPageToLoad("30000");
        selenium.type("id=continue-to-room-selection_booking_itemName", "etyey");
        selenium.type("id=continue-to-room-selection_booking_itemPrice", "54");
        selenium.click("id=continue-to-room-selection_0");
        selenium.waitForPageToLoad("30000");
        selenium.click("id=begin-booking_0");
        selenium.waitForPageToLoad("30000");
        selenium.type("id=continue-to-room-selection_booking_itemName", "gfeg");
        selenium.type("id=continue-to-room-selection_booking_itemPrice", "4545");
        selenium.click("id=continue-to-room-selection_0");
        selenium.waitForPageToLoad("30000");
        selenium.click("xpath=(//input[@id='continue-to-amenity-selection_0'])[3]");
        selenium.waitForPageToLoad("30000");
        selenium.click("id=continue-to-amenity-selection_0");
        selenium.waitForPageToLoad("30000");
        selenium.click("id=continue-to-amenity-selection_0");
        selenium.waitForPageToLoad("30000");
        selenium.click("id=end-checkout_0");
        selenium.waitForPageToLoad("30000");
        selenium.click("xpath=(//input[@id='begin-checkout_0'])[3]");
        selenium.waitForPageToLoad("30000");
        selenium.click("id=begin-checkout_0");
        selenium.waitForPageToLoad("30000");
        selenium.click("id=begin-checkout_0");
        selenium.waitForPageToLoad("30000");
        selenium.click("xpath=(//input[@id='end-checkout_0'])[3]");
        selenium.waitForPageToLoad("30000");
        selenium.click("id=end-checkout_0");
        selenium.waitForPageToLoad("30000");
        selenium.click("id=end-checkout_0");
    }

    @After
    public void tearDown() throws Exception {
        selenium.stop();
    }
}
