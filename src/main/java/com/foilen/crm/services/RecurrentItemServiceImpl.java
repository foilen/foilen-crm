/*
    Foilen CRM
    https://github.com/foilen/foilen-crm
    Copyright (c) 2015-2021 Foilen (https://foilen.com)

    The MIT License
    http://opensource.org/licenses/MIT

 */
package com.foilen.crm.services;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import com.foilen.crm.db.entities.invoice.Client;
import com.foilen.crm.web.model.CreateRecurrentItem;
import com.foilen.crm.web.model.UpdateRecurrentItem;
import com.foilen.smalltools.restapi.model.FormResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Service;

import com.foilen.crm.db.dao.ItemDao;
import com.foilen.crm.db.dao.RecurrentItemDao;
import com.foilen.crm.db.entities.invoice.Item;
import com.foilen.crm.db.entities.invoice.RecurrentItem;
import com.foilen.crm.web.model.RecurrentItemList;
import com.foilen.smalltools.tools.DateTools;

@Service
@Transactional
public class RecurrentItemServiceImpl extends AbstractApiService implements RecurrentItemService {

    @Autowired
    private ItemDao itemDao;
    @Autowired
    private RecurrentItemDao recurrentItemDao;

    @Override
    public void generateReady(Date now) {
        List<RecurrentItem> recurrentItems = recurrentItemDao.findAllByNextGenerationDateLessThanEqual(now);
        recurrentItems.forEach(recurrentItem -> {
            logger.info("Creating {} that was due on {}", recurrentItem, DateTools.formatFull(recurrentItem.getNextGenerationDate()));

            // Create Item
            itemDao.save(new Item(recurrentItem.getClient(), null, now, recurrentItem.getDescription(), recurrentItem.getPrice(), recurrentItem.getCategory()));

            // Update Recurrent Item
            recurrentItem.setNextGenerationDate(DateTools.addDate(recurrentItem.getNextGenerationDate(), recurrentItem.getCalendarUnit(), recurrentItem.getDelta()));
            recurrentItemDao.save(recurrentItem);

        });
    }

    @Override
    public RecurrentItemList listAll(String userId, int pageId) {

        // Validation
        validatePageId(pageId);
        entitlementService.canViewRecurrentItemOrFail(userId);

        // Retrieve
        RecurrentItemList result = new RecurrentItemList();
        Page<RecurrentItem> page = recurrentItemDao
                .findAll(PageRequest.of(pageId - 1, paginationService.getItemsPerPage(), Sort.by(Order.asc("client.name"), Order.desc("category"), Order.desc("description"), Order.asc("id"))));
        paginationService.wrap(result, page, com.foilen.crm.web.model.RecurrentItem.class);
        return result;
    }

    @Override
    public FormResult create(String userId, CreateRecurrentItem form) {
        FormResult formResult = new FormResult();
        // Validation
        entitlementService.canCreateItemOrFail(userId);
        validateMandatory(formResult, "clientShortName", form.getClientShortName());
        validateDateOnly(formResult, "nextGenerationDate", form.getNextGenerationDate());
        validateMandatory(formResult, "nextGenerationDate", form.getNextGenerationDate());
        validateMandatory(formResult, "description", form.getDescription());
        validateMandatory(formResult, "category", form.getCategory());
        Client client = validateClientByShortName(formResult, "clientShortName", form.getClientShortName());

        if (!formResult.isSuccess()) {
            return formResult;
        }

        RecurrentItem entity = getRecurrentItemFromCreate(form, client);
        recurrentItemDao.save(entity);

        return formResult;
    }

    @Override
    public FormResult delete(long id) {
        FormResult formResult = new FormResult();

        recurrentItemDao.deleteById(id);

        return formResult;
    }

    @Override
    public FormResult update(String userId, long id, UpdateRecurrentItem form) {
        FormResult formResult = new FormResult();
        // Validation
        entitlementService.canCreateItemOrFail(userId);
        validateMandatory(formResult, "clientShortName", form.getClientShortName());
        validateDateOnly(formResult, "nextGenerationDate", form.getNextGenerationDate());
        validateMandatory(formResult, "nextGenerationDate", form.getNextGenerationDate());
        validateMandatory(formResult, "description", form.getDescription());
        validateMandatory(formResult, "category", form.getCategory());
        Client client = validateClientByShortName(formResult, "clientShortName", form.getClientShortName());

        if (!formResult.isSuccess()) {
            return formResult;
        }

        RecurrentItem entity = recurrentItemDao.getOne(id);
        updateRecurrentItem(entity, form, client);

        recurrentItemDao.save(entity);

        return formResult;
    }

    private RecurrentItem updateRecurrentItem(RecurrentItem entity, UpdateRecurrentItem form, Client client) {
        entity.setDelta(form.getDelta());
        entity.setDescription(form.getDescription());
        entity.setPrice(form.getPrice());
        entity.setCategory(form.getCategory());
        entity.setCalendarUnit(getCalendarUnit(form.getFrequency()));
        entity.setClient(client);
        entity.setNextGenerationDate(DateTools.parseDateOnly(form.getNextGenerationDate()));

        return entity;
    }

    private RecurrentItem getRecurrentItemFromCreate(CreateRecurrentItem form, Client client) {
        RecurrentItem entity = new RecurrentItem();
        entity.setDelta(form.getDelta());
        entity.setDescription(form.getDescription());
        entity.setPrice(form.getPrice());
        entity.setCategory(form.getCategory());
        entity.setCalendarUnit(getCalendarUnit(form.getFrequency()));
        entity.setClient(client);
        entity.setNextGenerationDate(DateTools.parseDateOnly(form.getNextGenerationDate()));

        return entity;
    }

    private int getCalendarUnit(String frequency) {
        switch (frequency) {
            case "Monthly":
                return Calendar.MONTH;
            case "Yearly":
                return Calendar.YEAR;
            default:
                return Calendar.WEEK_OF_YEAR;
        }
    }

}
