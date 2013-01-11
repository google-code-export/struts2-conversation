package com.google.code.rees.scope.mocks.actions.conversation;

import org.apache.struts2.convention.annotation.Action;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.code.rees.scope.conversation.annotations.ConversationController;
import com.google.code.rees.scope.conversation.annotations.ConversationField;
import com.google.code.rees.scope.mocks.beans.TestBean;
import com.google.code.rees.scope.session.SessionField;
import com.opensymphony.xwork2.ActionSupport;

@ConversationController("oopy")
public class MockConversationController extends ActionSupport {
	
	private static final long serialVersionUID = 1L;
	@ConversationField private String conversationString = "initialState";
	@SessionField public String chubby;
	
	@Autowired
	private TestBean bean;

	@Action("begin")
	public String begin() {
		chubby = "chubbs";
		return SUCCESS;
	}
	
	@Action("do1")
	public String doThing1() {
		bean.setEcho("hello spring");
		return SUCCESS;
	}

	@Action("do2")
	public String doThing2() {
		return SUCCESS;
	}
	
	@Action("end")
	public String end() {
		return SUCCESS;
	}

	public void setConversationString(String conversationString) {
		this.conversationString = conversationString;
	}

	public String getConversationString() {
		return conversationString;
	}

	public void setBean(TestBean bean) {
		this.bean = bean;
	}

	public TestBean getBean() {
		return bean;
	}
}
