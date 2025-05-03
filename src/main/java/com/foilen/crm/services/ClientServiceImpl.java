package com.foilen.crm.services;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.foilen.crm.db.dao.ClientDao;
import com.foilen.crm.db.entities.invoice.Client;
import com.foilen.crm.db.entities.invoice.TechnicalSupport;
import com.foilen.crm.web.model.ClientList;
import com.foilen.crm.web.model.CreateOrUpdateClientForm;
import com.foilen.smalltools.reflection.BeanPropertiesCopierTools;
import com.foilen.smalltools.restapi.model.FormResult;
import com.foilen.smalltools.tools.JsonTools;
import com.foilen.smalltools.tools.StringTools;
import com.google.common.base.Strings;

@Service
@Transactional
public class ClientServiceImpl extends AbstractApiService implements ClientService {

    @Autowired
    private ClientDao clientDao;

    @Override
    public FormResult create(String userId, CreateOrUpdateClientForm form) {

        FormResult formResult = new FormResult();

        // Validation
        entitlementService.canCreateClientOrFail(userId);
        validateMandatory(formResult, "name", form.getName());
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
    public FormResult delete(String userId, String clientShortName) {

        FormResult formResult = new FormResult();

        // Validation
        entitlementService.canDeleteClientOrFail(userId);
        Client client = validateClientByShortName(formResult, "clientShortName", clientShortName);

        if (!formResult.isSuccess()) {
            return formResult;
        }

        // Delete
        clientDao.delete(client);

        return formResult;
    }

    @Override
    public ClientList listAll(String userId, int pageId, String search) {

        // Validation
        validatePageId(pageId);
        entitlementService.canViewClientOrFail(userId);

        if (Strings.isNullOrEmpty(search)) {
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

    @Override
    public FormResult update(String userId, String clientShortName, CreateOrUpdateClientForm form) {
        FormResult formResult = new FormResult();

        // Validation
        entitlementService.canUpdateClientOrFail(userId);
        Client client = validateClientByShortName(formResult, "clientShortName", clientShortName);
        validateMandatory(formResult, "name", form.getName());
        validateMandatory(formResult, "shortName", form.getShortName());
        if (!Strings.isNullOrEmpty(form.getShortName()) && !StringTools.safeEquals(clientShortName, form.getShortName())) {
            validateClientShortNameNotUsed(formResult, "shortName", form.getShortName());
        }
        validateMandatory(formResult, "contactName", form.getContactName());
        validateMandatory(formResult, "email", form.getEmail());
        validateEmail(formResult, "email", form.getEmail());
        validateMandatory(formResult, "lang", form.getLang());
        validateLanguage(formResult, "lang", form.getLang());
        TechnicalSupport technicalSupport = validateTechnicalSupport(formResult, "technicalSupportSid", form.getTechnicalSupportSid());

        if (!formResult.isSuccess()) {
            return formResult;
        }

        // Update
        new BeanPropertiesCopierTools(form, client).copyAllSameProperties();
        client.setTechnicalSupport(technicalSupport);

        clientDao.save(client);

        return formResult;
    }

}
