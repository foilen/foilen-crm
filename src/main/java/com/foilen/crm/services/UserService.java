package com.foilen.crm.services;

import com.foilen.crm.web.model.UpdateUserAdminForm;
import com.foilen.crm.web.model.UserList;
import com.foilen.smalltools.restapi.model.FormResult;

public interface UserService {

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

    FormResult updateAdmin(String userId, String id, UpdateUserAdminForm form);

}
