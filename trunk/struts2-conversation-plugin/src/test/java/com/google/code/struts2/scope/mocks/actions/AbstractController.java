package com.google.code.struts2.scope.mocks.actions;

import com.google.code.struts2.scope.sessionfield.SessionField;

public abstract class AbstractController {

	@SessionField
	private String abstractField = "hola";
	
	private String workflowString = "oopy doopy";

	public void setAbstractField(String abstractField) {
		this.abstractField = abstractField;
	}

	public String getAbstractField() {
		return abstractField;
	}

	public void setWorkflowString(String workflowString) {
		this.workflowString = workflowString;
	}

	public String getWorkflowString() {
		return workflowString;
	}

}
