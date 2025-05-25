package com.foilen.crm.services;

import com.foilen.crm.web.model.BillSomePendingItems;
import com.foilen.crm.web.model.CreateItemWithTime;
import com.foilen.crm.web.model.CreateOrUpdateItem;
import com.foilen.crm.web.model.ItemList;
import com.foilen.smalltools.restapi.model.FormResult;

import java.util.List;

public interface ItemService {

    FormResult billPending(String userId, String invoicePrefix);

    FormResult billSomePending(String userId, BillSomePendingItems form);

    FormResult create(String userId, CreateItemWithTime form);

    FormResult create(String userId, CreateOrUpdateItem form);

    FormResult delete(String userId, long id);

    /**
     * Get the list of items that were billed.
     *
     * @param userId the user that wants the list
     * @param pageId the page id starting at 1
     * @return the list of items
     */
    ItemList listBilled(String userId, int pageId);

    /**
     * Get the list of distinct categories from all items.
     *
     * @return the list of distinct categories sorted alphabetically
     */
    List<String> listDistinctCategories();

    /**
     * Get the list of items that are not billed yet.
     *
     * @param userId the user that wants the list
     * @param pageId the page id starting at 1
     * @return the list of items
     */
    ItemList listPending(String userId, int pageId);

    FormResult update(String userId, long id, CreateOrUpdateItem form);
}
