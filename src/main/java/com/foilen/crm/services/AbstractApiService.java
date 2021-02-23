/*
    Foilen CRM
    https://github.com/foilen/foilen-crm
    Copyright (c) 2015-2021 Foilen (https://foilen.com)

    The MIT License
    http://opensource.org/licenses/MIT

 */
package com.foilen.crm.services;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;

import com.foilen.crm.db.dao.ClientDao;
import com.foilen.crm.db.dao.TechnicalSupportDao;
import com.foilen.crm.db.entities.invoice.Client;
import com.foilen.crm.db.entities.invoice.TechnicalSupport;
import com.foilen.crm.exception.ErrorMessageException;
import com.foilen.smalltools.restapi.model.FormResult;
import com.foilen.smalltools.tools.AbstractBasics;
import com.foilen.smalltools.tools.CollectionsTools;
import com.foilen.smalltools.tools.DateTools;
import com.foilen.smalltools.tools.StringTools;
import com.google.common.base.Strings;

public abstract class AbstractApiService extends AbstractBasics {

    private static final Set<String> VALID_LANGS = Collections.unmodifiableSet(new HashSet<>(Arrays.asList("EN", "FR")));

    @Autowired
    protected ClientDao clientDao;
    @Autowired
    protected EntitlementService entitlementService;
    @Autowired
    protected PaginationService paginationService;
    @Autowired
    protected TechnicalSupportDao technicalSupportDao;

    protected Client validateClientByShortName(FormResult formResult, String fieldName, String clientShortName) {

        if (Strings.isNullOrEmpty(clientShortName)) {
            return null;
        }

        Client client = clientDao.findByShortName(clientShortName);
        if (client == null) {
            CollectionsTools.getOrCreateEmptyArrayList(formResult.getValidationErrorsByField(), fieldName, String.class).add("error.clientNotExist");
            return null;
        }

        return client;
    }

    protected void validateClientNameNotUsed(FormResult formResult, String fieldName, String clientName) {

        if (Strings.isNullOrEmpty(clientName)) {
            return;
        }

        Client client = clientDao.findByName(clientName);
        if (client != null) {
            CollectionsTools.getOrCreateEmptyArrayList(formResult.getValidationErrorsByField(), fieldName, String.class).add("error.alreadyTaken");
        }

    }

    protected void validateClientShortNameNotUsed(FormResult formResult, String fieldName, String clientShortName) {

        if (Strings.isNullOrEmpty(clientShortName)) {
            return;
        }

        Client client = clientDao.findByShortName(clientShortName);
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

    protected TechnicalSupport validateTechnicalSupport(FormResult formResult, String fieldName, String technicalSupportSid) {

        if (Strings.isNullOrEmpty(technicalSupportSid)) {
            return null;
        }

        TechnicalSupport technicalSupport = technicalSupportDao.findBySid(technicalSupportSid);
        if (technicalSupport == null) {
            CollectionsTools.getOrCreateEmptyArrayList(formResult.getValidationErrorsByField(), fieldName, String.class).add("error.technicalSupportNotExist");
            return null;
        }

        return technicalSupport;

    }

    protected TechnicalSupport validateTechnicalSupportByClient(FormResult formResult, String clientFieldName, Client client) {

        if (client == null) {
            return null;
        }

        TechnicalSupport technicalSupport = client.getTechnicalSupport();
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

        TechnicalSupport technicalSupport = technicalSupportDao.findBySid(sid);
        if (technicalSupport != null) {
            CollectionsTools.getOrCreateEmptyArrayList(formResult.getValidationErrorsByField(), fieldName, String.class).add("error.alreadyTaken");
        }
    }
}
