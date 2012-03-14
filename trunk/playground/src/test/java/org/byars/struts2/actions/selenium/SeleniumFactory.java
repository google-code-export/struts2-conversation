package org.byars.struts2.actions.selenium;

import com.thoughtworks.selenium.Selenium;

public interface SeleniumFactory {

    public void setBaseUrl(String baseUrl);

    public Selenium createSelenium();

}
