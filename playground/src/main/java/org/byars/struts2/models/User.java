package org.byars.struts2.models;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("conversation")
public class User {

    private String accountName;
    private Integer accountNumber;
    private String accountNumberAsString;

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountNumber(Integer accountNumber) {
        this.accountNumber = accountNumber;
        this.accountNumberAsString = this.accountNumber.toString();
    }

    public Integer getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumberAsString(String accountNumberAsString) {
        this.accountNumberAsString = accountNumberAsString;
    }

    public String getAccountNumberAsString() {
        return accountNumberAsString;
    }

}
