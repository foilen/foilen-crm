package com.foilen.crm.services;

import com.foilen.crm.db.repository.ClientRepository;
import com.foilen.crm.db.repository.TechnicalSupportRepository;
import com.foilen.crm.db.entities.invoice.Client;
import com.foilen.crm.db.entities.invoice.TechnicalSupport;
import com.foilen.crm.web.model.CreateOrUpdateTechnicalSupportForm;
import com.foilen.crm.web.model.TechnicalSupportList;
import com.foilen.smalltools.reflection.BeanPropertiesCopierTools;
import com.foilen.smalltools.restapi.model.FormResult;
import com.foilen.smalltools.tools.JsonTools;
import com.foilen.smalltools.tools.StringTools;
import com.google.common.base.Strings;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class TechnicalSupportServiceImpl extends AbstractApiService implements TechnicalSupportService {

    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private TechnicalSupportRepository technicalSupportRepository;

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
        technicalSupportRepository.save(entity);

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
        List<Client> clients = clientRepository.findAllByTechnicalSupportId(technicalSupport.getId());
        clients.forEach(client -> client.setTechnicalSupportId(null));
        clientRepository.saveAll(clients);

        // Delete
        technicalSupportRepository.delete(technicalSupport);

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
            page = technicalSupportRepository.findAll(PageRequest.of(pageId - 1, paginationService.getItemsPerPage(), Direction.ASC, "sid"));
        } else {
            page = technicalSupportRepository.findAllSearch(search, PageRequest.of(pageId - 1, paginationService.getItemsPerPage(), Direction.ASC, "sid"));
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

        technicalSupportRepository.save(technicalSupport);

        return formResult;
    }

}
