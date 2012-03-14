package org.byars.struts2.actions;

import com.thoughtworks.selenium.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.regex.Pattern;

public class CoopMilstripControllerTest extends SeleneseTestCase {
	@Before
	public void setUp() throws Exception {
		selenium = new DefaultSelenium("localhost", 4444, "*chrome", "https://dod-emall-coop.csd.disa.mil/");
		selenium.start();
	}

	@Test
	public void testCoopMilstripController() throws Exception {
		selenium.open("/acct/");
		selenium.click("id=button_id");
		selenium.waitForPageToLoad("30000");
		selenium.click("id=button_id");
		selenium.waitForPageToLoad("30000");
		selenium.type("id=authenticationForm_username", "SCRAUSER");
		selenium.type("id=authenticationForm_password", "2wsxcde3$RFVBGT%");
		selenium.click("id=button_id");
		selenium.waitForPageToLoad("30000");
		selenium.click("link=MOES");
		selenium.waitForPageToLoad("30000");
		selenium.waitForPageToLoad("30000");
		selenium.click("link=exact:A0A: Standard Requisitions");
		selenium.waitForPageToLoad("30000");
		selenium.type("id=RIC", "sdf");
		selenium.type("id=NSN", "111111111111111");
		selenium.type("id=unitOfIssue", "er");
		selenium.type("id=quantity", "1");
		selenium.type("id=dodaac", "w33333");
		selenium.type("id=originationDate", "1111");
		selenium.type("id=serialNumber", "1111");
		selenium.click("id=enter-milstrip_submit-milstrip-done");
		selenium.waitForPageToLoad("30000");
		verifyTrue(selenium.isTextPresent("NSN must be numbers only, with length of 13, for a DIC of A0A"));
	}

	@After
	public void tearDown() throws Exception {
		selenium.stop();
	}
}
