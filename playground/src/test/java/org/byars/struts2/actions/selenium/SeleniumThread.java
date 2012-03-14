package org.byars.struts2.actions.selenium;

import com.thoughtworks.selenium.Selenium;

public interface SeleniumThread extends EasyThread {

    public void setSelenium(Selenium selenium);

    public void addOperation(SeleniumOperation operation);

}
