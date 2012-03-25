package com.google.code.rees.scope.struts2;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.dispatcher.ServletRedirectResult;

import com.google.code.rees.scope.conversation.ConversationAdapter;
import com.google.code.rees.scope.util.RedirectUtil;

/**
 * 
 * @author rees.byars
 * 
 */
public class ConversationRedirectResult extends ServletRedirectResult {

    private static final long serialVersionUID = 2405888426696017242L;

    /**
     * Sends the redirection with the conversation IDs included as parameters
     * 
     * @param response
     *        The response
     * @param finalLocation
     *        The location URI
     * @throws IOException
     */
    @Override
    protected void sendRedirect(HttpServletResponse response,
            String finalLocation) throws IOException {

        String finalLocationWithConversationIds = RedirectUtil
                .getUrlParamString(finalLocation, ConversationAdapter
                        .getAdapter().getViewContext());

        super.sendRedirect(response, finalLocationWithConversationIds);

    }

}
