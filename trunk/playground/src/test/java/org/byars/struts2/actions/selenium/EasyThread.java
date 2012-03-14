package org.byars.struts2.actions.selenium;

public interface EasyThread extends Runnable {

    public void start();

    public void stop();

    public void yield();
}
