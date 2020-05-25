/*
    Foilen CRM
    https://github.com/foilen/foilen-crm
    Copyright (c) 2015-2020 Foilen (http://foilen.com)

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

import com.foilen.crm.db.dao.ClientDao;
import com.foilen.crm.db.entities.invoice.Client;
import com.foilen.crm.db.entities.invoice.TechnicalSupport;
import com.foilen.crm.web.model.ClientList;
import com.foilen.crm.web.model.CreateClient;
import com.foilen.smalltools.restapi.model.FormResult;
import com.foilen.smalltools.tools.JsonTools;

@Service
@Transactional
public class ClientServiceImpl extends AbstractApiService implements ClientService {

    @Autowired
    private ClientDao clientDao;

    @Override
    public FormResult create(String userId, CreateClient form) {

        FormResult formResult = new FormResult();

        // Validation
        entitlementService.canCreateClientOrFail(userId);
        validateMandatory(formResult, "name", form.getName());
        validateClientNameNotUsed(formResult, "name", form.getName());
        validateMandatory(formResult, "shortName", form.getShortName());
        validateClientShortNameNotUsed(formResult, "shortName", form.getShortName());
        validateMandatory(formResult, "contactName", form.getContactName());
        validateMandatory(formResult, "email", form.getEmail());
        validateEmail(formResult, "email", form.getEmail());
        validateMandatory(formResult, "lang", form.getLang());
        validateLanguage(formResult, "lang", form.getLang());
        TechnicalSupport technicalSupport = validateTechnicalSupport(formResult, "technicalSupportSid", form.getTechnicalSupportSid());

        if (!formResult.isSuccess()) {
            return formResult;
        }

        // Create
        Client entity = JsonTools.clone(form, Client.class);
        entity.setTechnicalSupport(technicalSupport);
        clientDao.save(entity);

        return formResult;
    }

    @Override
    public ClientList listAll(String userId, int pageId, String search) {

        // Validation
        validatePageId(pageId);
        entitlementService.canViewClientOrFail(userId);

        if (Strings.isBlank(search)) {
            search = null;
        }

        // Retrieve
        ClientList result = new ClientList();
        Page<Client> page;
        if (search == null) {
            page = clientDao.findAll(PageRequest.of(pageId - 1, paginationService.getItemsPerPage(), Direction.ASC, "name"));
        } else {
            search = "%" + search + "%";
            page = clientDao.findAllSearch(search, PageRequest.of(pageId - 1, paginationService.getItemsPerPage(), Direction.ASC, "name"));
        }
        paginationService.wrap(result, page, com.foilen.crm.web.model.Client.class);
        return result;
    }

}
