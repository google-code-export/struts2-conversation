package com.github.overengineer.scope;

public interface CommonConstants {
	
	interface Defaults {
		
		/**
		 * 5 minutes
		 */
		long MONITORING_FREQUENCY = 300000;
		int MONITORING_THREAD_POOL_SIZE = 4;
		
	}
	
	interface Properties {

		String MONITORING_FREQUENCY = "overengineer.monitoring.frequency";
		String MONITORING_THREAD_POOL_SIZE = "overengineer.monitoring.thread.pool.size";
		
	}

}
