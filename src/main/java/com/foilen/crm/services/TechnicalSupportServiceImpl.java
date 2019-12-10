/*
    Foilen CRM
    https://github.com/foilen/foilen-crm
    Copyright (c) 2015-2019 Foilen (http://foilen.com)

    The MIT License
    http://opensource.org/licenses/MIT

 */
package com.foilen.crm.services;

import javax.transaction.Transactional;

import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.foilen.crm.db.dao.TechnicalSupportDao;
import com.foilen.crm.db.entities.invoice.TechnicalSupport;
import com.foilen.crm.web.model.TechnicalSupportList;

@Service
@Transactional
public class TechnicalSupportServiceImpl extends AbstractApiService implements TechnicalSupportService {

    @Autowired
    private TechnicalSupportDao technicalSupportDao;

    @Override
    public TechnicalSupportList listAll(String userId, int pageId, String search) {

        // Validation
        validatePageId(pageId);
        entitlementService.canViewTechnicalSupportOrFail(userId);

        if (Strings.isBlank(search)) {
            search = null;
        }

        // Retrieve
        TechnicalSupportList result = new TechnicalSupportList();
        Page<TechnicalSupport> page;
        if (search == null) {
            page = technicalSupportDao.findAll(PageRequest.of(pageId - 1, paginationService.getItemsPerPage(), Direction.ASC, "sid"));
        } else {
            search = "%" + search + "%";
            page = technicalSupportDao.findAllSearch(search, PageRequest.of(pageId - 1, paginationService.getItemsPerPage(), Direction.ASC, "sid"));
        }
        paginationService.wrap(result, page, com.foilen.crm.web.model.TechnicalSupport.class);
        return result;
    }

}
