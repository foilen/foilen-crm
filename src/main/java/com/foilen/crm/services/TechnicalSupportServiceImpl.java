/*
    Foilen CRM
    https://github.com/foilen/foilen-crm
    Copyright (c) 2015-2021 Foilen (https://foilen.com)

    The MIT License
    http://opensource.org/licenses/MIT

 */
package com.foilen.crm.services;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.foilen.crm.db.dao.ClientDao;
import com.foilen.crm.db.dao.TechnicalSupportDao;
import com.foilen.crm.db.entities.invoice.Client;
import com.foilen.crm.db.entities.invoice.TechnicalSupport;
import com.foilen.crm.web.model.CreateOrUpdateTechnicalSupportForm;
import com.foilen.crm.web.model.TechnicalSupportList;
import com.foilen.smalltools.reflection.BeanPropertiesCopierTools;
import com.foilen.smalltools.restapi.model.FormResult;
import com.foilen.smalltools.tools.JsonTools;
import com.foilen.smalltools.tools.StringTools;
import com.google.common.base.Strings;

@Service
@Transactional
public class TechnicalSupportServiceImpl extends AbstractApiService implements TechnicalSupportService {

    @Autowired
    private TechnicalSupportDao technicalSupportDao;

    @Autowired
    private ClientDao clientDao;

    @Override
    public FormResult create(String userId, CreateOrUpdateTechnicalSupportForm form) {

        FormResult formResult = new FormResult();

        // Validation
        entitlementService.canCreateTechnicalSupportOrFail(userId);
        validateMandatory(formResult, "sid", form.getSid());
        validateTechnicalSupportSidNotUsed(formResult, "sid", form.getSid());

        if (!formResult.isSuccess()) {
            return formResult;
        }

        // Create
        TechnicalSupport entity = JsonTools.clone(form, TechnicalSupport.class);
        technicalSupportDao.save(entity);

        return formResult;
    }

    @Override
    public FormResult delete(String userId, String technicalSupportSid) {

        FormResult formResult = new FormResult();

        // Validation
        entitlementService.canDeleteTechnicalSupportOrFail(userId);
        TechnicalSupport technicalSupport = validateTechnicalSupport(formResult, "technicalSupportSid", technicalSupportSid);

        if (!formResult.isSuccess()) {
            return formResult;
        }

        // Detach from clients
        List<Client> clients = clientDao.findByTechnicalSupport(technicalSupport);
        clients.forEach(client -> client.setTechnicalSupport(null));

        // Delete
        technicalSupportDao.delete(technicalSupport);

        return formResult;

    }

    @Override
    public TechnicalSupportList listAll(String userId, int pageId, String search) {

        // Validation
        validatePageId(pageId);
        entitlementService.canViewTechnicalSupportOrFail(userId);

        if (Strings.isNullOrEmpty(search)) {
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
    public FormResult update(String userId, String technicalSupportSid, CreateOrUpdateTechnicalSupportForm form) {
        FormResult formResult = new FormResult();

        // Validation
        entitlementService.canUpdateTechnicalSupportOrFail(userId);
        TechnicalSupport technicalSupport = validateTechnicalSupport(formResult, "technicalSupportSid", technicalSupportSid);
        validateMandatory(formResult, "sid", form.getSid());
        if (!Strings.isNullOrEmpty(form.getSid()) && !StringTools.safeEquals(technicalSupportSid, form.getSid())) {
            validateTechnicalSupportSidNotUsed(formResult, "sid", form.getSid());
        }

        if (!formResult.isSuccess()) {
            return formResult;
        }

        // Update
        new BeanPropertiesCopierTools(form, technicalSupport).copyAllSameProperties();

        technicalSupportDao.save(technicalSupport);

        return formResult;
    }

}
