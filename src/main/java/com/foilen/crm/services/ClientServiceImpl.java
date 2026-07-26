package com.foilen.crm.services;

import com.foilen.crm.db.repository.ClientRepository;
import com.foilen.crm.db.repository.TransactionRepository;
import com.foilen.crm.db.entities.invoice.Client;
import com.foilen.crm.db.entities.invoice.TechnicalSupport;
import com.foilen.crm.web.model.ClientList;
import com.foilen.crm.web.model.CreateOrUpdateClientForm;
import com.foilen.smalltools.reflection.BeanPropertiesCopierTools;
import com.foilen.smalltools.restapi.model.FormResult;
import com.foilen.smalltools.tools.JsonTools;
import com.foilen.smalltools.tools.StringTools;
import com.google.common.base.Strings;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional
public class ClientServiceImpl extends AbstractApiService implements ClientService {

    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private TransactionRepository transactionRepository;

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
        entity.setTechnicalSupportId(technicalSupport == null ? null : technicalSupport.getId());
        clientRepository.save(entity);

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

        // Delete the client and everything referencing it (no DB-level cascade with MongoDB)
        itemRepository.deleteAllByClientId(client.getId());
        recurrentItemRepository.deleteAllByClientId(client.getId());
        transactionRepository.deleteAllByClientId(client.getId());
        clientRepository.delete(client);

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
        Pageable pageable = PageRequest.of(pageId - 1, paginationService.getItemsPerPage(), Direction.ASC, "name");
        Page<Client> page;
        if (entitlementService.isAdmin(userId)) {
            if (search == null) {
                page = clientRepository.findAll(pageable);
            } else {
                page = clientRepository.findAllSearch(search, pageable);
            }
        } else {
            // Non-admins only see the client(s) sharing their own email
            page = clientRepository.findAllByEmailIgnoreCase(userId, pageable);
        }
        paginationService.wrap(result, page, com.foilen.crm.web.model.ClientExtended.class);

        // Resolve the technicalSupportId reference on each item
        Map<String, com.foilen.crm.web.model.TechnicalSupport> technicalSupports = technicalSupportsByIds(page.getContent().stream()
                .map(Client::getTechnicalSupportId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet()));
        var items = result.getItems();
        for (int i = 0; i < items.size(); i++) {
            items.get(i).setTechnicalSupport(technicalSupports.get(page.getContent().get(i).getTechnicalSupportId()));
        }

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
        client.setTechnicalSupportId(technicalSupport == null ? null : technicalSupport.getId());

        clientRepository.save(client);

        return formResult;
    }

}
