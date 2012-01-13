package com.google.code.struts2.scope.mocks.actions;


import com.google.code.struts2.scope.sessionfield.SessionField;
import com.opensymphony.xwork2.Action;

public class MockActionInterfaceImpl implements Action {
	
	@SessionField
	private String sessionString;
	
	private String workflowString;

	@Override
	public String execute() throws Exception {
		return SUCCESS;
	}

	public void setSessionString(String sessionString) {
		this.sessionString = sessionString;
	}

	public String getSessionString() {
		return sessionString;
	}

	public void setWorkflowString(String workflowField) {
		this.workflowString = workflowField;
	}

	public String getWorkflowString() {
		return workflowString;
	}

}
