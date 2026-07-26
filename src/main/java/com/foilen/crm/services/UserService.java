package com.foilen.crm.services;

import com.foilen.crm.web.model.ChangePasswordForm;
import com.foilen.crm.web.model.LoginForm;
import com.foilen.crm.web.model.LoginWithCodeForm;
import com.foilen.crm.web.model.LoginWithCodeRequestForm;
import com.foilen.crm.web.model.UpdateUserAdminForm;
import com.foilen.crm.web.model.UpdateUserDisabledForm;
import com.foilen.crm.web.model.UserList;
import com.foilen.smalltools.restapi.model.FormResult;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface UserService {

    FormResult changePassword(String userId, ChangePasswordForm form);

    /**
     * Get the list of users.
     *
     * @param userId
     *            the user that wants the list
     * @param pageId
     *            the page id starting at 1
     * @param search
     *            (optional) search
     * @return the list of users
     */
    UserList listAll(String userId, int pageId, String search);

    FormResult login(LoginForm form, HttpServletRequest request, HttpServletResponse response);

    FormResult loginWithCode(LoginWithCodeForm form, HttpServletRequest request, HttpServletResponse response);

    FormResult loginWithCodeRequest(LoginWithCodeRequestForm form);

    FormResult logout(HttpServletRequest request, HttpServletResponse response);

    FormResult updateAdmin(String userId, String id, UpdateUserAdminForm form);

    FormResult updateDisabled(String userId, String id, UpdateUserDisabledForm form);

}
