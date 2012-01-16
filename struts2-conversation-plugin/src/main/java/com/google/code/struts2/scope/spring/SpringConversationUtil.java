package com.google.code.struts2.scope.spring;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;

import com.google.code.struts2.scope.conversation.ConversationConstants;
import com.opensymphony.xwork2.ActionContext;

public class SpringConversationUtil {

	@SuppressWarnings("unchecked")
	public static <T> T getConversationField(String fieldName) {
		T field = null;
		ActionContext context = ActionContext.getContext();
		if (context != null) {
			Map<String, Object> session = context.getSession();
			for (String conversationId : getConversationIds()) {
				Map<String, Object> conversationFieldMap = (Map<String, Object>) session.get(conversationId);
				if (conversationFieldMap != null) {
					field = (T) conversationFieldMap.get(fieldName);
					if (field != null) break;
				}
			}
		}
		return field;
	}
	
	public static String[] getConversationIds() {
		
		List<String> convoIds = new ArrayList<String>();

		HttpServletRequest request = ServletActionContext.getRequest();
		if (request != null) {
			Map<String, String[]> params = request.getParameterMap();
			for (Entry<String, String[]> param : params.entrySet()) {
				if (param.getKey().endsWith(ConversationConstants.CONVERSATION_NAME_SESSION_MAP_SUFFIX)) {
					convoIds.add(param.getValue()[0]);
				}
			}
		}
		
		return convoIds.toArray(new String[convoIds.size()]);
	}
}
