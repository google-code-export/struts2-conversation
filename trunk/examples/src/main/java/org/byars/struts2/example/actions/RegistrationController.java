package org.byars.struts2.example.actions;

import org.byars.struts2.example.models.RegistrationContext;
import org.byars.struts2.example.services.RegistrationService;

import com.google.code.rees.scope.conversation.annotations.ConversationController;
import com.google.code.rees.scope.conversation.annotations.ConversationField;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ModelDriven;
import com.opensymphony.xwork2.inject.Inject;

@ConversationController
public class RegistrationController implements ModelDriven<RegistrationContext> {

    private static final long serialVersionUID = -4758539468646746488L;
    private RegistrationService service;

    @ConversationField
    private RegistrationContext model;

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

    @Override
    public RegistrationContext getModel() {
        return this.model;
    }

    private void setModel(RegistrationContext model) {
        this.model = model;
    }

}
