package com.foilen.crm.services;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;

import com.foilen.crm.db.repository.ClientRepository;
import com.foilen.crm.db.repository.ItemRepository;
import com.foilen.crm.db.repository.RecurrentItemRepository;
import com.foilen.crm.db.repository.TechnicalSupportRepository;
import com.foilen.crm.db.repository.UserRepository;
import com.foilen.crm.db.entities.invoice.Client;
import com.foilen.crm.db.entities.invoice.Item;
import com.foilen.crm.db.entities.invoice.RecurrentItem;
import com.foilen.crm.db.entities.invoice.TechnicalSupport;
import com.foilen.crm.db.entities.user.User;
import com.foilen.crm.exception.ErrorMessageException;
import com.foilen.crm.web.model.ClientShort;
import com.foilen.smalltools.restapi.model.FormResult;
import com.foilen.smalltools.restapi.services.PaginationService;
import com.foilen.smalltools.tools.AbstractBasics;
import com.foilen.smalltools.tools.CollectionsTools;
import com.foilen.smalltools.tools.DateTools;
import com.foilen.smalltools.tools.JsonTools;
import com.foilen.smalltools.tools.StringTools;
import com.google.common.base.Strings;

public abstract class AbstractApiService extends AbstractBasics {

    private static final Set<String> VALID_LANGS = Collections.unmodifiableSet(new HashSet<>(Arrays.asList("EN", "FR")));

    @Autowired
    protected ClientRepository clientRepository;
    @Autowired
    protected EntitlementService entitlementService;
    @Autowired
    protected ItemRepository itemRepository;
    @Autowired
    protected PaginationService paginationService;
    @Autowired
    protected RecurrentItemRepository recurrentItemRepository;
    @Autowired
    protected TechnicalSupportRepository technicalSupportRepository;
    @Autowired
    protected UserRepository userRepository;

    /**
     * Batch-resolves clientIds into their {@link ClientShort} projection (name/shortName/email/lang),
     * for enriching a page of API results whose entity only stores a clientId reference.
     */
    protected Map<String, ClientShort> clientShortsByIds(Collection<String> clientIds) {
        return clientRepository.findAllById(clientIds).stream()
                .collect(Collectors.toMap(Client::getId, c -> JsonTools.clone(c, ClientShort.class)));
    }

    /**
     * Batch-resolves technicalSupportIds into their API model, for enriching a page of API results
     * whose entity only stores a technicalSupportId reference.
     */
    protected Map<String, com.foilen.crm.web.model.TechnicalSupport> technicalSupportsByIds(Collection<String> technicalSupportIds) {
        return technicalSupportRepository.findAllById(technicalSupportIds).stream()
                .collect(Collectors.toMap(TechnicalSupport::getId, ts -> JsonTools.clone(ts, com.foilen.crm.web.model.TechnicalSupport.class)));
    }

    protected Client validateClientByShortName(FormResult formResult, String fieldName, String clientShortName) {

        if (Strings.isNullOrEmpty(clientShortName)) {
            return null;
        }

        Client client = clientRepository.findByShortName(clientShortName);
        if (client == null) {
            CollectionsTools.getOrCreateEmptyArrayList(formResult.getValidationErrorsByField(), fieldName, String.class).add("error.clientNotExist");
            return null;
        }

        return client;
    }

    protected void validateClientShortNameNotUsed(FormResult formResult, String fieldName, String clientShortName) {

        if (Strings.isNullOrEmpty(clientShortName)) {
            return;
        }

        Client client = clientRepository.findByShortName(clientShortName);
        if (client != null) {
            CollectionsTools.getOrCreateEmptyArrayList(formResult.getValidationErrorsByField(), fieldName, String.class).add("error.alreadyTaken");
        }

    }

    protected void validateDateOnly(FormResult formResult, String fieldName, String date) {

        if (Strings.isNullOrEmpty(date)) {
            return;
        }

        boolean goodFormat = false;
        try {
            String expectedDate = DateTools.formatDateOnly(DateTools.parseDateOnly(date));
            goodFormat = StringTools.safeEquals(expectedDate, date);
        } catch (Exception e) {
        }

        if (!goodFormat) {
            CollectionsTools.getOrCreateEmptyArrayList(formResult.getValidationErrorsByField(), fieldName, String.class).add("error.dateDayOnlyFormat");
        }

    }

    protected void validateEmail(FormResult formResult, String fieldName, String value) {
        if (!Strings.isNullOrEmpty(value) && !EmailValidator.getInstance().isValid(value)) {
            CollectionsTools.getOrCreateEmptyArrayList(formResult.getValidationErrorsByField(), fieldName, String.class).add("error.notEmail");
        }
    }

    protected Item validateItemById(FormResult formResult, String fieldName, String id) {

        Item item = itemRepository.findById(id).orElse(null);
        if (item == null) {
            CollectionsTools.getOrCreateEmptyArrayList(formResult.getValidationErrorsByField(), fieldName, String.class).add("error.itemNotExist");
            return null;
        }

        return item;
    }

    protected void validateItemIsPending(FormResult formResult, String fieldName, Item item) {

        if (item == null) {
            return;
        }

        if (item.getInvoiceId() != null) {
            CollectionsTools.getOrCreateEmptyArrayList(formResult.getValidationErrorsByField(), fieldName, String.class).add("error.itemIsNotPending");
        }

    }

    protected void validateLanguage(FormResult formResult, String fieldName, String value) {
        if (!Strings.isNullOrEmpty(value) && !VALID_LANGS.contains(value)) {
            CollectionsTools.getOrCreateEmptyArrayList(formResult.getValidationErrorsByField(), fieldName, String.class).add("error.notValidLanguage");
        }
    }

    protected void validateMandatory(FormResult formResult, String fieldName, String value) {
        if (Strings.isNullOrEmpty(value)) {
            CollectionsTools.getOrCreateEmptyArrayList(formResult.getValidationErrorsByField(), fieldName, String.class).add("error.mandatory");
        }
    }

    protected void validatePageId(int pageId) {
        if (pageId < 1) {
            throw new ErrorMessageException("error.pageStart1");
        }
    }

    protected RecurrentItem validateRecurrentItem(FormResult formResult, String fieldName, String id) {

        RecurrentItem entity = recurrentItemRepository.findById(id).orElse(null);
        if (entity == null) {
            CollectionsTools.getOrCreateEmptyArrayList(formResult.getValidationErrorsByField(), fieldName, String.class).add("error.recurrentItemNotExist");
            return null;
        }

        return entity;

    }

    protected TechnicalSupport validateTechnicalSupport(FormResult formResult, String fieldName, String sid) {

        if (Strings.isNullOrEmpty(sid)) {
            return null;
        }

        TechnicalSupport entity = technicalSupportRepository.findBySid(sid);
        if (entity == null) {
            CollectionsTools.getOrCreateEmptyArrayList(formResult.getValidationErrorsByField(), fieldName, String.class).add("error.technicalSupportNotExist");
            return null;
        }

        return entity;

    }

    protected TechnicalSupport validateTechnicalSupportByClient(FormResult formResult, String clientFieldName, Client client) {

        if (client == null) {
            return null;
        }

        TechnicalSupport technicalSupport = client.getTechnicalSupportId() == null ? null : technicalSupportRepository.findById(client.getTechnicalSupportId()).orElse(null);
        if (technicalSupport == null) {
            CollectionsTools.getOrCreateEmptyArrayList(formResult.getValidationErrorsByField(), clientFieldName, String.class).add("error.clientWithoutTechnicalSupport");
            return null;
        }

        return technicalSupport;
    }

    protected void validateTechnicalSupportSidNotUsed(FormResult formResult, String fieldName, String sid) {

        if (Strings.isNullOrEmpty(sid)) {
            return;
        }

        TechnicalSupport technicalSupport = technicalSupportRepository.findBySid(sid);
        if (technicalSupport != null) {
            CollectionsTools.getOrCreateEmptyArrayList(formResult.getValidationErrorsByField(), fieldName, String.class).add("error.alreadyTaken");
        }
    }

    protected User validateUserById(FormResult formResult, String fieldName, String id) {

        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            CollectionsTools.getOrCreateEmptyArrayList(formResult.getValidationErrorsByField(), fieldName, String.class).add("error.userNotExist");
            return null;
        }

        return user;
    }

}
