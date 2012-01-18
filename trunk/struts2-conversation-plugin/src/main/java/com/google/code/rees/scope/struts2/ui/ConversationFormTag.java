package com.google.code.rees.scope.struts2.ui;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Component;
import org.apache.struts2.views.jsp.ui.FormTag;

import com.opensymphony.xwork2.util.ValueStack;

public class ConversationFormTag extends FormTag {
	
	private static final long serialVersionUID = -7730872356094866344L;
	
	public Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
		return new ConversationForm(stack, req, res);
	}

}
