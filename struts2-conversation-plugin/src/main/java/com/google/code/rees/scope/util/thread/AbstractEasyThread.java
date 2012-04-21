package com.google.code.rees.scope.util.thread;

/**
 * @author rees.byars
 */
public abstract class AbstractEasyThread implements EasyThread {

	protected volatile Thread thread;
	protected boolean isRunning;

	protected AbstractEasyThread() {
		this.isRunning = false;
		this.thread = new Thread(new Runner(this));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized void start() {
		if (!this.isRunning) {
			this.isRunning = true;
			this.thread.start();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized void stop() {
		this.isRunning = false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized void destroy() {
		this.stop();
		Thread dieingThread = this.thread;
		this.thread = null;
		if (dieingThread != null) {
			dieingThread.interrupt();
		}
	}

	protected abstract void doWhileRunning();

	class Runner implements Runnable {

		private AbstractEasyThread owner;

		protected Runner(AbstractEasyThread owner) {
			this.owner = owner;
		}

		@Override
		public void run() {
			while (this.owner.isRunning) {
				this.owner.doWhileRunning();
				Thread.yield();
			}
		}

	}

}
