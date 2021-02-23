/*
    Foilen CRM
    https://github.com/foilen/foilen-crm
    Copyright (c) 2015-2021 Foilen (https://foilen.com)

    The MIT License
    http://opensource.org/licenses/MIT

 */
package com.foilen.crm.services;

import javax.transaction.Transactional;

import com.foilen.crm.db.dao.ClientDao;
import com.foilen.crm.db.entities.invoice.Client;
import com.foilen.crm.web.model.CreateTechnicalSupport;
import com.foilen.crm.web.model.UpdateTechnicalSupport;
import com.foilen.smalltools.restapi.model.FormResult;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.foilen.crm.db.dao.TechnicalSupportDao;
import com.foilen.crm.db.entities.invoice.TechnicalSupport;
import com.foilen.crm.web.model.TechnicalSupportList;

import java.util.List;

@Service
@Transactional
public class TechnicalSupportServiceImpl extends AbstractApiService implements TechnicalSupportService {

    @Autowired
    private TechnicalSupportDao technicalSupportDao;

    @Autowired
    private ClientDao clientDao;

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

    @Override
    public FormResult create(String name, CreateTechnicalSupport form) {
        FormResult formResult = new FormResult();

        // Validation
        entitlementService.canCreateTechnicalSupportOrFail(name);
        validateMandatory(formResult, "sid", form.getSid());
        validateTechnicalSupportSidNotUsed(formResult, "sid", form.getSid());
        validateMandatory(formResult, "pricePerHour", form.getPricePerHourFormatted());

        TechnicalSupport technicalSupport = new TechnicalSupport();
        technicalSupport.setSid(form.getSid());
        technicalSupport.setPricePerHour(form.getPricePerHour());

        technicalSupportDao.save(technicalSupport);

        if (!formResult.isSuccess()) {
            return formResult;
        }

        return formResult;
    }

    @Override
    public FormResult update(String name, String sid, UpdateTechnicalSupport form) {
        FormResult formResult = new FormResult();

        entitlementService.canCreateTechnicalSupportOrFail(name);
        validateMandatory(formResult, "sid", form.getSid());
        validateMandatory(formResult, "pricePerHour", form.getPricePerHourFormatted());

        if (!formResult.isSuccess()) {
            return formResult;
        }

        TechnicalSupport technicalSupport = technicalSupportDao.findBySid(sid);
        technicalSupport.setPricePerHour(form.getPricePerHour());

        technicalSupportDao.save(technicalSupport);

        return formResult;
    }

    @Override
    public FormResult delete(String sid) {
        FormResult formResult = new FormResult();
        TechnicalSupport technicalSupport = technicalSupportDao.findBySid(sid);

        List<Client> clients = clientDao.findByTechnicalSupport(technicalSupport);
        clients.forEach(client -> client.setTechnicalSupport(null));

        technicalSupportDao.delete(technicalSupport);

        return formResult;
    }

}
