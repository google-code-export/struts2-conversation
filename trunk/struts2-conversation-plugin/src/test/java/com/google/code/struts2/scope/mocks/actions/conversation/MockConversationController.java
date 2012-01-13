package com.google.code.struts2.scope.mocks.actions.conversation;

import org.apache.struts2.convention.annotation.Action;
import com.google.code.struts2.scope.conversation.ConversationController;
import com.google.code.struts2.scope.sessionfield.SessionField;
import com.opensymphony.xwork2.ActionSupport;

@ConversationController("sdf:sdfsf,fff")
public class MockConversationController extends ActionSupport {
	
	private static final long serialVersionUID = 1L;
	private String conversationString = "initialState";
	@SessionField String chubby;

	@Action("begin")
	public String begin() {
		chubby = "chubbs";
		return SUCCESS;
	}
	
	@Action("do1")
	public String doThing1() {
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
}
