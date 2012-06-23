package org.byars.struts2.example.models;

import java.io.Serializable;

public class RegistrationContext implements Serializable {

    private static final long serialVersionUID = 8352422130293072891L;

    private ContactInformation contact = new ContactInformation();
    private MusicPreferences preferences = new MusicPreferences();

    public ContactInformation getContact() {
        return contact;
    }

    public void setContact(ContactInformation contact) {
        this.contact = contact;
    }

    public MusicPreferences getPreferences() {
        return preferences;
    }

    public void setPreferences(MusicPreferences preferences) {
        this.preferences = preferences;
    }

}
