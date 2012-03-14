package org.byars.struts2.actions.selenium;

/**
 * @author rees.byars
 */
public abstract class AbstractEasyThread implements EasyThread {

    protected Thread thread;
    protected boolean isRunning;

    public AbstractEasyThread() {
        this.isRunning = false;
        this.thread = new Thread(this);
    }

    @Override
    public void run() {
        while (this.isRunning) {
            this.doWhileRunning();
            this.yield();
        }
    }

    @Override
    public synchronized void start() {
        if (!this.isRunning) {
            this.isRunning = true;
            this.thread.start();
        }
    }

    @Override
    public synchronized void stop() {
        this.isRunning = false;
    }

    @Override
    public void yield() {
        Thread.yield();
    }

    public abstract void doWhileRunning();

}
