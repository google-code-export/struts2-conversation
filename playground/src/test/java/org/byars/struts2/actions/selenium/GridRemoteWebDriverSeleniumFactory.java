package org.byars.struts2.actions.selenium;

import java.net.MalformedURLException;
import java.net.URL;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverBackedSelenium;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.selenium.Selenium;

public class GridRemoteWebDriverSeleniumFactory implements SeleniumFactory {

    protected static final Logger LOG = LoggerFactory
            .getLogger(GridRemoteWebDriverSeleniumFactory.class);

    protected URL gridUrl;
    protected String baseUrl;

    public GridRemoteWebDriverSeleniumFactory() {
        try {
            this.gridUrl = new URL("http://localhost:4444/wd/hub");
        } catch (MalformedURLException e) {
            LOG.error("Could not form valid URL for the Selenium grid!");
        }
    }

    @Override
    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Override
    public Selenium createSelenium() {
        WebDriver driver = new RemoteWebDriver(this.gridUrl,
                DesiredCapabilities.firefox());
        return new WebDriverBackedSelenium(driver, this.baseUrl);
    }
}
