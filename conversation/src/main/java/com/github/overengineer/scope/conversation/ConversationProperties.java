package com.github.overengineer.scope.conversation;

import java.io.Serializable;

public interface ConversationProperties extends Serializable {
	String getActionSuffix();
	long getMaxIdleTime();
	int getMonitoringThreadPoolSize();
	long getMonitoringFrequency();
	int getMaxInstances();
}
