package org.byars.struts2.example.actions;

import org.byars.struts2.example.models.RegistrationContext;
import org.byars.struts2.example.services.RegistrationService;

import com.google.code.rees.scope.struts2.ModelDrivenConversationSupport;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.inject.Inject;

public class RegistrationController extends
        ModelDrivenConversationSupport<RegistrationContext> {

    private static final long serialVersionUID = -4758539468646746488L;
    private RegistrationService service;

    public String begin() {
        this.setModel(this.getService().getNewContextInstance());
        return Action.SUCCESS;
    }

    public String contact() {
        return Action.SUCCESS;
    }

    public String preferences() {
        return Action.SUCCESS;
    }

    public String end() {
        this.getService().register(this.getModel());
        return Action.SUCCESS;
    }

    @Inject
    public void setService(RegistrationService service) {
        this.service = service;
    }

    protected RegistrationService getService() {
        return this.service;
    }

}
