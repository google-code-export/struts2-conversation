package com.google.code.struts2.scope.mocks.actions;

import org.apache.struts2.convention.annotation.Action;

import com.google.code.struts2.scope.request.RequestField;
import com.google.code.struts2.scope.sessionfield.SessionField;

public class MockConventionController extends AbstractController {
	
	public static final String SUCCESS = "success";
	public static final String IN_PROGRESS = "in.progress";
	public static final String NOT_IN_PROGRESS = "not.in.progress";
	
	@SessionField
	private String sessionString = NOT_IN_PROGRESS;
	
	@RequestField
	private int dumb = 2;
	
	@SessionField
	private int smart = 2;
	
	@Action(value="begin")
	public String begin() {
		setSessionString(IN_PROGRESS);
		smart = 3;
		setDumb(3);
		setWorkflowString("hungee");
		return SUCCESS;
	}
	
	@Action(value="continue1")
	public String continue1() {
		return SUCCESS;
	}

	@Action(value="continue2")
	public String continue2() {
		return SUCCESS;
	}
	
	@Action(value="end")
	public String end() {
		setSessionString(NOT_IN_PROGRESS);
		return SUCCESS;
	}

	public void setSessionString(String sessionString) {
		this.sessionString = sessionString;
	}

	public String getSessionString() {
		return sessionString;
	}

	public void setDumb(int dumb) {
		this.dumb = dumb;
	}

	public int getDumb() {
		return dumb;
	}
	
	public void setSmart(int smart) {
		this.smart = smart;
	}
	
	public int getSmart() {
		return smart;
	}

}
