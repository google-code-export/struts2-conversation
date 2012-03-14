package org.byars.struts2.actions;

import org.junit.*;

public class DallasReads extends MoesSeleneseTest  {

	@Test
	public void testSdfgsdfg() throws Exception {
		selenium.click("link=exact:A0A: Standard Requisitions");
		selenium.waitForPageToLoad("30000");
		selenium.selectWindow("null");
		selenium.type("id=RIC", "sdf");
		selenium.type("id=NSN", "11");
		selenium.click("id=enter-milstrip_submit-milstrip-done");
		selenium.waitForPageToLoad("30000");
		verifyTrue(selenium.isTextPresent("NSN must be numbers only, with length of 13, for a DIC of A0A"));
	}

}
