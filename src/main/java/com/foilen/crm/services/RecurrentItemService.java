package com.foilen.crm.services;

import java.util.Date;

import com.foilen.crm.web.model.CreateOrUpdateRecurrentItemForm;
import com.foilen.crm.web.model.RecurrentItemList;
import com.foilen.smalltools.restapi.model.FormResult;

public interface RecurrentItemService {

    FormResult create(String userId, CreateOrUpdateRecurrentItemForm form);

    FormResult delete(String userId, long id);

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

    FormResult update(String userId, long id, CreateOrUpdateRecurrentItemForm form);
}
