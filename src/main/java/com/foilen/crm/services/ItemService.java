/*
    Foilen CRM
    https://github.com/foilen/foilen-crm
    Copyright (c) 2015-2021 Foilen (https://foilen.com)

    The MIT License
    http://opensource.org/licenses/MIT

 */
package com.foilen.crm.services;

import com.foilen.crm.web.model.BillSomePendingItems;
import com.foilen.crm.web.model.CreateItem;
import com.foilen.crm.web.model.CreateItemWithTime;
import com.foilen.crm.web.model.ItemList;
import com.foilen.crm.web.model.UpdateItem;
import com.foilen.smalltools.restapi.model.FormResult;

public interface ItemService {

    FormResult billPending(String userId, String invoicePrefix);

    FormResult billSomePending(String userId, BillSomePendingItems form);

    FormResult create(String userId, CreateItem form);

    FormResult create(String userId, CreateItemWithTime form);

    FormResult delete(long id);

    /**
     * Get the list of items that were billed.
     *
     * @param userId
     *            the user that wants the list
     * @param pageId
     *            the page id starting at 1
     * @return the list of items
     */
    ItemList listBilled(String userId, int pageId);

    /**
     * Get the list of items that are not billed yet.
     *
     * @param userId
     *            the user that wants the list
     * @param pageId
     *            the page id starting at 1
     * @return the list of items
     */
    ItemList listPending(String userId, int pageId);

    FormResult update(String userId, UpdateItem form);
}
