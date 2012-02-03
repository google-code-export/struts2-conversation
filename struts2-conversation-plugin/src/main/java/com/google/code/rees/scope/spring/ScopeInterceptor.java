package com.google.code.rees.scope.spring;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.google.code.rees.scope.conversation.ConversationConfigurationProvider;
import com.google.code.rees.scope.conversation.ConversationManager;

public class ScopeInterceptor implements HandlerInterceptor {
	
	protected ConversationManager conversationManager;
	
	public ScopeInterceptor(ConversationManager conversationManager, ConversationConfigurationProvider conversationConfigurationProvider) {
		this.conversationManager = conversationManager;
		this.conversationManager.init(conversationConfigurationProvider);
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception exception)
			throws Exception {
		Collection<SpringConversationPostProcessorWrapper> postProcessors = 
			SpringConversationAdapter.getSpringAdapter().getPostProcessors();
		for (SpringConversationPostProcessorWrapper processor : postProcessors) {
			processor.postProcessConversation();
		}
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response,
			Object handler, ModelAndView modelAndView) throws Exception {
		modelAndView.addAllObjects(SpringConversationAdapter.getSpringAdapter().getViewContext());
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		conversationManager.processConversations(new SpringConversationAdapter(request, (HandlerMethod) handler));
		return true;
	}

}
