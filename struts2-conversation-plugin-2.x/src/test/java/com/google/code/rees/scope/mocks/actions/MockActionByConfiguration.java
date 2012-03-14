package com.google.code.rees.scope.mocks.actions;

public class MockActionByConfiguration {

    private static final String SUCCESS = "success";

    private String sessionField;

    public String execute() {
        return SUCCESS;
    }

    public void setSessionField(String sessionField) {
        System.out.println("****************************************"
                + sessionField);
        this.sessionField = sessionField;
    }

    public String getSessionField() {
        return sessionField;
    }

}
