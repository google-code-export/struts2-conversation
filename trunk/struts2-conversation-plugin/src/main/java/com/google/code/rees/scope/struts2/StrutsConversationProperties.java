package com.google.code.rees.scope.struts2;

import com.google.code.rees.scope.conversation.ConversationProperties;
import com.opensymphony.xwork2.inject.Inject;

public class StrutsConversationProperties implements ConversationProperties {

	private static final long serialVersionUID = 2714692166710182286L;
	
	protected long maxIdleTime;
    protected int monitoringThreadPoolSize;
    protected long monitoringFrequency;
    protected int maxInstances;
    protected String actionSuffix;
    
    @Inject(ConventionConstants.ACTION_SUFFIX)
    public void setActionSuffix(String suffix) {
        this.actionSuffix = suffix;
    }
    
    @Inject(StrutsScopeConstants.CONVERSATION_IDLE_TIMEOUT)
    public void setDefaultMaxIdleTime(String defaultMaxIdleTimeString) {
        this.maxIdleTime = Long.parseLong(defaultMaxIdleTimeString);
    }
    
    @Inject(StrutsScopeConstants.CONVERSATION_MONITORING_THREAD_POOL_SIZE)
    public void setMonitoringThreadPoolSize(String monitoringThreadPoolSizeString) {
    	this.monitoringThreadPoolSize = Integer.parseInt(monitoringThreadPoolSizeString);
    }

    @Inject(StrutsScopeConstants.CONVERSATION_MONITORING_FREQUENCY)
    public void setMonitoringFrequency(String monitoringFrequencyString) {
    	this.monitoringFrequency = Long.parseLong(monitoringFrequencyString);
    }

    @Inject(StrutsScopeConstants.CONVERSATION_MAX_INSTANCES)
    public void setMaxInstances(String maxInstancesString) {
    	this.maxInstances = Integer.parseInt(maxInstancesString);
    }

	@Override
	public long getMaxIdleTime() {
		return this.maxIdleTime;
	}

	@Override
	public int getMonitoringThreadPoolSize() {
		return this.monitoringThreadPoolSize;
	}

	@Override
	public long getMonitoringFrequency() {
		return this.monitoringFrequency;
	}

	@Override
	public int getMaxInstances() {
		return this.maxInstances;
	}

	@Override
	public String getActionSuffix() {
		return this.actionSuffix;
	}

}
