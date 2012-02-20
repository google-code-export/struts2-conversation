package com.google.code.rees.scope.spring;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.google.code.rees.scope.conversation.ConversationAdapter;
import com.google.code.rees.scope.conversation.ConversationManager;
import com.google.code.rees.scope.conversation.context.ConversationContextManager;
import com.google.code.rees.scope.conversation.context.HttpConversationContextManagerFactory;

/**
 * This Spring MVC interceptor uses a {@link ConversationManager} to
 * process conversations before and after controller execution.
 * 
 * @author rees.byars
 */
public class ConversationInterceptor implements HandlerInterceptor {

    protected ConversationManager conversationManager;
    protected HttpConversationContextManagerFactory conversationContextManagerFactory;

    /**
     * Set the {@link ConversationManager}
     * 
     * @param conversationManager
     */
    public void setConversationManager(ConversationManager conversationManager) {
        this.conversationManager = conversationManager;
    }

    /**
     * Set the {@link HttpConversationContextManagerFactory}
     * 
     * @param conversationContextManagerFactory
     */
    public void setConversationContextManagerFactory(
            HttpConversationContextManagerFactory conversationContextManagerFactory) {
        this.conversationContextManagerFactory = conversationContextManagerFactory;
    }

    /**
     * Calls {@link ConversationAdapter#executePostProcessors()}
     */
    @Override
    public void afterCompletion(HttpServletRequest request,
            HttpServletResponse response, Object handler, Exception exception)
            throws Exception {
        ConversationAdapter.getAdapter().executePostProcessors();
    }

    /**
     * This method not used by the Interceptor
     */
    @Override
    public void postHandle(HttpServletRequest request,
            HttpServletResponse response, Object handler,
            ModelAndView modelAndView) throws Exception {
        // do nothing
    }

    /**
     * Calls
     * {@link ConversationManager#processConversations(ConversationAdapter)} and
     * passes in a {@link SpringConversationAdapter}.
     */
    @Override
    public boolean preHandle(HttpServletRequest request,
            HttpServletResponse response, Object handler) throws Exception {
        ConversationContextManager contextManager = conversationContextManagerFactory
                .getManager(request);
        ConversationAdapter adapter = new SpringConversationAdapter(request,
                (HandlerMethod) handler, contextManager);
        conversationManager.processConversations(adapter);
        return true;
    }

}
