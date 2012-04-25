package com.google.code.rees.scope.util;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.google.code.rees.scope.conversation.ConversationUtil;

public class RedirectUtilTest {

    @Test
    public void testGetUrlParamString() {
        // TODO make into an actual test!!!
        String url = "http://www.test.com/";
        Map<String, String> viewContext = new HashMap<String, String>();
        System.out.println(RedirectUtil.getUrlParamString(url, viewContext));
        viewContext.put("test1-conversation", ConversationUtil.generateId());
        System.out.println(RedirectUtil.getUrlParamString(url, viewContext));
        viewContext.put("test2-conversation", ConversationUtil.generateId());
        System.out.println(RedirectUtil.getUrlParamString(url, viewContext));

        url = "http://www.test.com/?someParam=someValue";
        viewContext = new HashMap<String, String>();
        System.out.println(RedirectUtil.getUrlParamString(url, viewContext));
        viewContext.put("test1-conversation", ConversationUtil.generateId());
        System.out.println(RedirectUtil.getUrlParamString(url, viewContext));
        viewContext.put("test2-conversation", ConversationUtil.generateId());
        System.out.println(RedirectUtil.getUrlParamString(url, viewContext));
    }

}
