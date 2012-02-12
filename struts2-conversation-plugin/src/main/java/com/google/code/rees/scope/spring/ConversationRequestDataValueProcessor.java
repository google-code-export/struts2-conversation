package com.google.code.rees.scope.spring;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.servlet.support.RequestDataValueProcessor;

import com.google.code.rees.scope.conversation.ConversationAdapter;

public class ConversationRequestDataValueProcessor implements
        RequestDataValueProcessor {

    @Override
    public String processAction(HttpServletRequest paramHttpServletRequest,
            String paramString) {
        // not used
        return null;
    }

    @Override
    public String processFormFieldValue(
            HttpServletRequest paramHttpServletRequest, String paramString1,
            String paramString2, String paramString3) {
        // not used
        return null;
    }

    @Override
    public Map<String, String> getExtraHiddenFields(
            HttpServletRequest paramHttpServletRequest) {
        return ConversationAdapter.getAdapter().getViewContext();
    }

    @Override
    public String processUrl(HttpServletRequest paramHttpServletRequest,
            String paramString) {
        // not used
        return null;
    }

}
