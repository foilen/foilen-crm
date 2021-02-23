/*
    Foilen CRM
    https://github.com/foilen/foilen-crm
    Copyright (c) 2015-2021 Foilen (https://foilen.com)

    The MIT License
    http://opensource.org/licenses/MIT

 */
package com.foilen.crm.services;

import com.foilen.crm.web.model.ClientList;
import com.foilen.crm.web.model.CreateClient;
import com.foilen.crm.web.model.UpdateClient;
import com.foilen.smalltools.restapi.model.FormResult;

public interface ClientService {

    FormResult create(String userId, CreateClient form);

    FormResult delete(long clientId);

    /**
     * Get the list of clients.
     *
     * @param userId
     *            the user that wants the list
     * @param pageId
     *            the page id starting at 1
     * @param search
     *            (optional) search
     * @return the list of clients
     */
    ClientList listAll(String userId, int pageId, String search);

    FormResult update(String userId, long clientId, UpdateClient form);
}
