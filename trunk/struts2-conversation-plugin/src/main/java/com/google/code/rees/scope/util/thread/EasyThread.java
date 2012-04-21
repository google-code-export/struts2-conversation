package com.google.code.rees.scope.util.thread;

/**
 * 
 * This interface provides a simple mechanism for interacting with {@link Thread Threads}.
 * 
 * @author rees.byars
 */
public interface EasyThread {

	/**
	 * Begins this thread.  If an underlying {@link Thread} does not already exist, it is created and started.  If it does already
	 * exist, its state is returned to running.
	 */
	public void start();

	/**
	 * Stops this thread.  The underlying {@link Thread} will still exist, but in effect it will cease to execute until it is started again.
	 */
	public void stop();

	/**
	 * Safely stops and destroys the underlying thread.  For the purposes of safety, the underlying thread cannot be guaranteed
	 * to completely, immediately cease, but it will be stopped as soon as possible in a safe manner.
	 */
	public void destroy();

}
