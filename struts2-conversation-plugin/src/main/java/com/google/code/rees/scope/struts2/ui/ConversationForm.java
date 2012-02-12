package com.google.code.rees.scope.struts2.ui;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Form;

import com.opensymphony.xwork2.util.ValueStack;

/**
 * 
 * @author rees.byars
 * 
 */
public class ConversationForm extends Form {

    public ConversationForm(ValueStack stack, HttpServletRequest request,
            HttpServletResponse response) {
        super(stack, request, response);
    }

    @Override
    protected String getDefaultTemplate() {
        return "conversation-form-close";
    }
}
