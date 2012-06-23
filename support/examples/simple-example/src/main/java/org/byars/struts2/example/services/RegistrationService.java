package org.byars.struts2.example.services;

import org.byars.struts2.example.models.RegistrationContext;

public interface RegistrationService {

    public RegistrationContext getNewContextInstance();

    public void register(RegistrationContext registrationContext);

}
