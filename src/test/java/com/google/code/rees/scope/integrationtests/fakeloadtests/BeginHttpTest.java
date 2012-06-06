/***********************************************************************************************************************
 *
 * Struts2-Conversation-Plugin - An Open Source Conversation- and Flow-Scope Solution for Struts2-based Applications
 * =================================================================================================================
 *
 * Copyright (C) 2012 by Rees Byars
 * http://code.google.com/p/struts2-conversation/
 *
 ***********************************************************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 ***********************************************************************************************************************
 *
 * $Id: BeginHttpTest.java Apr 30, 2012 11:54:41 PM reesbyars $
 *
 **********************************************************************************************************************/
package com.google.code.rees.scope.integrationtests.fakeloadtests;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.google.code.rees.scope.util.thread.BasicTaskThread;
import com.google.code.rees.scope.util.thread.TaskThread;


/**
 * @author rees.byars
 *
 */
public class BeginHttpTest {
	
	HttpClient client;
	
	@Before
	public void setUp() {
		HttpParams params = new BasicHttpParams();
		HttpConnectionParams.setSoTimeout(params, 3000);
	    SchemeRegistry registry = new SchemeRegistry();
	    registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
	    ClientConnectionManager cm = new ThreadSafeClientConnManager(params, registry);
	    this.client = new DefaultHttpClient(cm, params);
	}
	
	@After
	public void tearDown() {
		this.client.getConnectionManager().shutdown();
	}
	
	@Ignore
	@Test
	public void testBegin() throws ClientProtocolException, IOException, InterruptedException {
		
		Set<TaskThread> taskThreads = new HashSet<TaskThread>();
		Set<GetTask> getTasks = new HashSet<GetTask>();
		
		for (int i = 0; i < 7; i++) {
			TaskThread thread = BasicTaskThread.spawnInstance();
			GetTask getTask = new GetTask(this.client, "http://localhost:7001/simple-example/registration/begin.action");
			thread.addTask(getTask);
			taskThreads.add(thread);
			getTasks.add(getTask);
		}
		
		Thread.sleep(60000L);
		
		for (TaskThread thread : taskThreads) {
			thread.stop();
		}
		
		int successCount = 0;
		int abortCount = 0;
		for (GetTask getTask : getTasks) {
			successCount += getTask.getSuccessCount();
			abortCount += getTask.getAbortCount();
		}
		
		System.out.println("Success Count:  " + successCount);
		System.out.println("Abort Count:  " + abortCount);
	}

}
