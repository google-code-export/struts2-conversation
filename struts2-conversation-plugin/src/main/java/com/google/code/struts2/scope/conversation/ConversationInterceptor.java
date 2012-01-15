package com.google.code.struts2.scope.conversation;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.interceptor.Interceptor;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;

/**
 * 
 * @author rees.byars
 */
public class ConversationInterceptor implements Interceptor {

	private static final long serialVersionUID = 4489190504064841648L;

	private static final Logger LOG = LoggerFactory
			.getLogger(ConversationInterceptor.class);

	protected ConversationManager manager;
	
	@Inject(ConversationConstants.MANAGER_KEY)
	public void setConversationManager(ConversationManager manager) {
		this.manager = manager;
	}

	@Override
	public void init() {
		LOG.info("Initializing the ConversationInterceptor...");
	}
	
	@Override
	public void destroy() {
		LOG.info("Destroying the ConversationInterceptor...");
	}

	public String intercept(ActionInvocation invocation) throws Exception {
		manager.processConversationFields(invocation);
		return invocation.invoke();
	}

}
