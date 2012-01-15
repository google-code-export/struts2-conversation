package com.google.code.struts2.scope.sessionfield;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import com.google.code.struts2.scope.ScopeUtil;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.interceptor.Interceptor;
import com.opensymphony.xwork2.interceptor.PreResultListener;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;

/**
 * 
 * @author rees.byars
 * 
 */
public class SessionFieldInterceptor implements Interceptor {
	
	private static final long serialVersionUID = -4878720217458532949L;
	private static final Logger LOG = LoggerFactory
			.getLogger(SessionFieldInterceptor.class);

	protected SessionFieldConfigBuilder configBuilder;
	protected SessionFieldConfig sessionFieldConfig;

	@Inject(SessionFieldConstants.CONFIG_BUILDER_KEY)
	public void setSessionFieldConfigBuilder(SessionFieldConfigBuilder configBuilder) {
		this.configBuilder = configBuilder;
	}

	@Override
	public void init() {
		LOG.info("Initializing the SessionFieldInterceptor...");
		if (configBuilder == null) {
			LOG.error("No SessionFieldConfigBuilder was found.  " +
					"Please make sure that a bean named " + 
					SessionFieldConstants.CONFIG_BUILDER_KEY +
					" of type " + SessionFieldConfigBuilder.class.getName() + 
					" is defined in a configuration file such as struts.xml.");
		} else {
			sessionFieldConfig = configBuilder.getSessionFieldConfig();
		}
	}
	
	@Override
	public void destroy() {
		LOG.info("Destroying the SessionFieldInterceptor...");
	}

	@Override
	public String intercept(ActionInvocation invocation) throws Exception {
		beforeInvocation(invocation);
		return invocation.invoke();
	}

	@SuppressWarnings("unchecked")
	protected void beforeInvocation(ActionInvocation invocation) {

		Object action = invocation.getAction();
		Class<?> actionClass = action.getClass();
		Map<String, Field> classFieldMap = sessionFieldConfig
				.getFields(actionClass);

		if (classFieldMap != null) {

			Map<String, Object> session = invocation.getInvocationContext()
					.getSession();

			Map<String, Object> sessionFieldMap = (Map<String, Object>) session
					.get(SessionFieldConstants.SESSION_FIELD_MAP_KEY);

			if (sessionFieldMap != null) {
				if (LOG.isDebugEnabled()) {
					LOG.debug("sessionFieldMap was not null - beginning setting of field values for the action "
							+ invocation.getProxy().getActionName());
				}

				ScopeUtil.setFieldValues(action,
						sessionFieldConfig.getFields(actionClass),
						sessionFieldMap);
			} else {
				if (LOG.isDebugEnabled()) {
					LOG.debug("sessionFieldMap was null - forgoing setting of field values for the action "
							+ invocation.getProxy().getActionName());
				}
				sessionFieldMap = new HashMap<String, Object>();
			}

			invocation.addPreResultListener(new SessionFieldResultListener(
					sessionFieldMap));

		} else if (LOG.isDebugEnabled()) {
			LOG.debug("No session field were found for the action "
				+ invocation.getProxy().getActionName());
		}

	}

	class SessionFieldResultListener implements PreResultListener {

		Map<String, Object> sessionFieldMap;

		SessionFieldResultListener(Map<String, Object> sessionFieldMap) {
			this.sessionFieldMap = sessionFieldMap;
		}

		@Override
		public void beforeResult(ActionInvocation invocation, String resultCode) {
			
			Object action = invocation.getAction();

			Map<String, Field> classFieldMap = sessionFieldConfig
					.getFields(action.getClass());

			if (classFieldMap != null) {

				if (LOG.isDebugEnabled()) {
					LOG.debug("Getting SessionField values following action execution from action of class "
							+ action.getClass());
				}

				Map<String, Object> classSessionFieldMap = ScopeUtil.getFieldValues(
						action, classFieldMap);

				sessionFieldMap.putAll(classSessionFieldMap);

				Map<String, Object> session = invocation.getInvocationContext()
						.getSession();

				session.put(SessionFieldConstants.SESSION_FIELD_MAP_KEY, sessionFieldMap);

			} else {
				if (LOG.isDebugEnabled()) {
					LOG.debug("No SessionFields found for class "
							+ action.getClass());
				}
			}

		}
	}

}
