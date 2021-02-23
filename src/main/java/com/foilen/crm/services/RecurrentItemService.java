/*
    Foilen CRM
    https://github.com/foilen/foilen-crm
    Copyright (c) 2015-2021 Foilen (https://foilen.com)

    The MIT License
    http://opensource.org/licenses/MIT

 */
package com.foilen.crm.services;

import java.util.Date;

import com.foilen.crm.web.model.CreateRecurrentItem;
import com.foilen.crm.web.model.RecurrentItemList;
import com.foilen.crm.web.model.UpdateRecurrentItem;
import com.foilen.smalltools.restapi.model.FormResult;

public interface RecurrentItemService {

    void generateReady(Date now);

    /**
     * Get the list of recurrentItems.
     *
     * @param userId
     *            the user that wants the list
     * @param pageId
     *            the page id starting at 1
     * @return the list of recurrentItems
     */
    RecurrentItemList listAll(String userId, int pageId);

    FormResult create(String userId, CreateRecurrentItem form);

    FormResult delete(long id);

    FormResult update(String userId, long id, UpdateRecurrentItem form);
}
