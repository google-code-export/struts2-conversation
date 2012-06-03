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
 * $Id: GetTask.java May 1, 2012 12:25:52 AM reesbyars $
 *
 **********************************************************************************************************************/
package com.google.code.rees.scope.integrationtests.fakeloadtests;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import com.google.code.rees.scope.util.thread.ThreadTask;

/**
 * @author rees.byars
 *
 */
public class GetTask implements ThreadTask {
	
	private final HttpClient httpClient;
    private final HttpContext context;
    private final String getUrl;
    boolean active = true;
    private int abortCount = 0;
    private int successCount = 0;
    
	public GetTask(HttpClient httpClient, String getUrl) {
        this.httpClient = httpClient;
        this.context = new BasicHttpContext();
        this.getUrl = getUrl;
    }
	
	@Override
	public boolean isActive() {
		return this.active;
	}
	
	@Override
	public void cancel() {
		this.active = false;
	}
	
	@Override
	public void doTask() {
		HttpGet httpGet = new HttpGet(this.getUrl);
		try {
			httpClient.execute(httpGet, context);
			this.successCount++;
		} catch (Exception e) {
			this.abortCount++;
			httpGet.abort();
		}
		Thread.yield();
	}
	
	public int getSuccessCount() {
		return this.successCount;
	}
	
	public int getAbortCount() {
		return this.abortCount;
	}

}
