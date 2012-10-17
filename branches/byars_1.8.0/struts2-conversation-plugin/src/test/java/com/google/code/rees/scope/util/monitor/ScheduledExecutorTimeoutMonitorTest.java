package com.google.code.rees.scope.util.monitor;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.code.rees.scope.conversation.context.ConversationContext;
import com.google.code.rees.scope.conversation.context.DefaultConversationContext;
import com.google.code.rees.scope.util.thread.BasicTaskThread;
import com.google.code.rees.scope.util.thread.TaskThread;
import com.google.code.rees.scope.util.thread.ThreadTask;

public class ScheduledExecutorTimeoutMonitorTest {
	
	private static final Logger LOG = LoggerFactory.getLogger(ScheduledExecutorTimeoutMonitorTest.class);
	
	long adds = 0L;
	long removes = 0L;
	
	@Test
	public void testConcurrency() throws InterruptedException {
		
		final ScheduledExecutorTimeoutMonitor<ConversationContext> m = new ScheduledExecutorTimeoutMonitor<ConversationContext>();
		
		m.setScheduler(Executors.newScheduledThreadPool(4));
		
		//final BasicTimeoutMonitor<ConversationContext> m = BasicTimeoutMonitor.spawnInstance();
		
		final LinkedBlockingQueue<ConversationContext> queue = new LinkedBlockingQueue<ConversationContext>();
		
		ThreadTask addingTask = new ThreadTask() {
			
			public boolean isActive() {return true;}
			public void cancel() {}
			public void doTask() {
				ConversationContext c = new DefaultConversationContext(
						UUID.randomUUID().toString(),
						UUID.randomUUID().toString(), 5000L);
				queue.add(c);
				m.addTimeoutable(c);
				adds++;
				Thread.yield();
			}
			
		};
		
		ThreadTask removingTask = new ThreadTask() {
			
			public boolean isActive() {return true;}
			public void cancel() {}
			public void doTask() {
				ConversationContext c = queue.poll();
				if (c != null) {
					m.removeTimeoutable(c);
					removes++;
				}
				Thread.yield();
			}
			
		};
		
		Set<TaskThread> addingThreads = new HashSet<TaskThread>();
		for (int i = 0; i < 4; i++) {
			TaskThread t = BasicTaskThread.spawnInstance();
			t.addTask(addingTask);
			addingThreads.add(t);
		}
		
		Thread.sleep(1500L);
		
		Set<TaskThread> removingThreads = new HashSet<TaskThread>();
		for (int i = 0; i < 4; i++) {
			TaskThread t = BasicTaskThread.spawnInstance();
			t.addTask(removingTask);
			removingThreads.add(t);
		}
		
		Thread.sleep(1500L);
		
		for (TaskThread t : addingThreads) {
			t.stop();
		}
		
		for (TaskThread t : removingThreads) {
			t.stop();
		}
		
		LOG.info("Results of concurrency testing of the scheduled executor timeout monitor:");
		LOG.info("Adds:  " + adds);
		LOG.info("Removes:  " + removes);
		LOG.info("Items held by monitor:  " + m.timeoutRunners.size());
	}

}
