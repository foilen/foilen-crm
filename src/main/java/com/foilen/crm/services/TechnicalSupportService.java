/*
    Foilen CRM
    https://github.com/foilen/foilen-crm
    Copyright (c) 2015-2021 Foilen (https://foilen.com)

    The MIT License
    http://opensource.org/licenses/MIT

 */
package com.foilen.crm.services;

import com.foilen.crm.web.model.CreateOrUpdateTechnicalSupportForm;
import com.foilen.crm.web.model.TechnicalSupportList;
import com.foilen.smalltools.restapi.model.FormResult;

public interface TechnicalSupportService {

    FormResult create(String userId, CreateOrUpdateTechnicalSupportForm form);

    FormResult delete(String userId, String technicalSupportSid);

    /**
     * Get the list of technical supports.
     *
     * @param userId
     *            the user that wants the list
     * @param pageId
     *            the page id starting at 1
     * @param search
     *            (optional) search
     * @return the list of technical supports
     */
    TechnicalSupportList listAll(String userId, int pageId, String search);

    FormResult update(String userId, String technicalSupportSid, CreateOrUpdateTechnicalSupportForm form);
}
