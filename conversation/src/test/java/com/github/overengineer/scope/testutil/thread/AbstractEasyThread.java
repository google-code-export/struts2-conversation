/*******************************************************************************
 * 
 *  Struts2-Conversation-Plugin - An Open Source Conversation- and Flow-Scope Solution for Struts2-based Applications
 *  =================================================================================================================
 * 
 *  Copyright (C) 2012 by Rees Byars
 *  http://code.google.com/p/struts2-conversation/
 * 
 * **********************************************************************************************************************
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 *  the License. You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 *  an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 *  specific language governing permissions and limitations under the License.
 * 
 * **********************************************************************************************************************
 * 
 *  $Id: AbstractEasyThread.java reesbyars $
 ******************************************************************************/
package com.github.overengineer.scope.testutil.thread;

/**
 * A simple abstract implementation of the {@link EasyThread} interface.
 * 
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
