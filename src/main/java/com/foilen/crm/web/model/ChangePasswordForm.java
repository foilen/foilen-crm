package com.foilen.crm.web.model;

import com.foilen.smalltools.restapi.model.AbstractApiBase;

public class ChangePasswordForm extends AbstractApiBase {

    private String currentPassword;
    private String newPassword;
    private String newPasswordConfirmation;

    public String getCurrentPassword() {
        return currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public String getNewPasswordConfirmation() {
        return newPasswordConfirmation;
    }

    public ChangePasswordForm setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
        return this;
    }

    public ChangePasswordForm setNewPassword(String newPassword) {
        this.newPassword = newPassword;
        return this;
    }

    public ChangePasswordForm setNewPasswordConfirmation(String newPasswordConfirmation) {
        this.newPasswordConfirmation = newPasswordConfirmation;
        return this;
    }

}
