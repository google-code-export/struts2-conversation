package com.google.code.rees.scope.mocks.actions;

import com.google.code.rees.scope.session.SessionField;

public class MockPojoController {
	
	private static final String SUCCESS = "success";
	
	@SessionField
	private String sessionField;

	public String execute() {
		return SUCCESS;
	}

	public void setSessionField(String sessionField) {
		this.sessionField = sessionField;
	}

	public String getSessionField() {
		return sessionField;
	}

}
