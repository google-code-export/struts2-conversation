package org.byars.struts2.actions;

import org.junit.After;
import org.junit.Before;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverBackedSelenium;
import org.openqa.selenium.firefox.FirefoxDriver;

import com.thoughtworks.selenium.SeleneseTestCase;

public class MoesSeleneseTest extends SeleneseTestCase  {
	
	@Before
	public void setUp() throws Exception {
		WebDriver driver = new FirefoxDriver();
		String baseUrl = "https://dod-emall-coop.csd.disa.mil/";
		selenium = new WebDriverBackedSelenium(driver, baseUrl);
		this.login();
	}
	
	@After
	public void tearDown() throws Exception {
		this.logout();
		selenium.stop();
	}
	
	protected void login() throws InterruptedException {
		selenium.open("/acct/");
		selenium.click("id=button_id");
		selenium.waitForPageToLoad("30000");
		selenium.click("id=button_id");
		selenium.waitForPageToLoad("30000");
		selenium.type("id=authenticationForm_username", "SCRAUSER");
		selenium.type("id=authenticationForm_password", "2wsxcde3$RFVBGT%");
		selenium.click("id=button_id");
		selenium.waitForPageToLoad("30000");
		selenium.mouseOver("id=tabTools");
		selenium.click("link=MOES");
		selenium.waitForPageToLoad("30000");
		for (int second = 0;; second++) {
			if (second >= 60) fail("timeout");
			try { if (selenium.isVisible("link=exact:A0A: Standard Requisitions")) break; } catch (Exception e) {}
			Thread.sleep(1000);
		}
	}
	
	protected void logout() {
		//TODO
	}

}
