package org.byars.struts2.example.services;

import org.byars.struts2.example.models.RegistrationContext;

public class RegistrationServiceImpl implements RegistrationService {

    public RegistrationContext getNewContextInstance() {
        return new RegistrationContext();
    }

    public void register(RegistrationContext registrationContext) {
        // Save to database, notify external service, etc.
    }

}
