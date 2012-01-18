package com.google.code.rees.scope.mocks.actions;


import com.google.code.rees.scope.sessionfield.SessionField;
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
