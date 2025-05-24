package com.foilen.crm.services;

import com.foilen.crm.db.entities.invoice.Client;
import com.foilen.crm.db.entities.invoice.Item;
import com.foilen.crm.db.entities.invoice.RecurrentItem;
import com.foilen.crm.web.model.CreateOrUpdateRecurrentItemForm;
import com.foilen.crm.web.model.RecurrentItemList;
import com.foilen.smalltools.reflection.BeanPropertiesCopierTools;
import com.foilen.smalltools.restapi.model.FormResult;
import com.foilen.smalltools.tools.DateTools;
import com.foilen.smalltools.tools.JsonTools;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@Transactional
public class RecurrentItemServiceImpl extends AbstractApiService implements RecurrentItemService {

    @Override
    public FormResult create(String userId, CreateOrUpdateRecurrentItemForm form) {

        FormResult formResult = new FormResult();

        // Validation
        entitlementService.canCreateRecurrentItemOrFail(userId);
        validateMandatory(formResult, "clientShortName", form.getClientShortName());
        validateMandatory(formResult, "nextGenerationDate", form.getNextGenerationDate());
        validateDateOnly(formResult, "nextGenerationDate", form.getNextGenerationDate());
        validateMandatory(formResult, "description", form.getDescription());
        validateMandatory(formResult, "category", form.getCategory());
        Client client = validateClientByShortName(formResult, "clientShortName", form.getClientShortName());

        if (!formResult.isSuccess()) {
            return formResult;
        }

        // Create
        RecurrentItem entity = JsonTools.clone(form, RecurrentItem.class);
        entity.setClient(client);
        entity.setNextGenerationDate(DateTools.parseDateOnly(form.getNextGenerationDate()));
        recurrentItemDao.save(entity);

        return formResult;

    }

    @Override
    public FormResult delete(String userId, long recurrentItemId) {

        FormResult formResult = new FormResult();

        // Validation
        entitlementService.canDeleteRecurrentItemOrFail(userId);
        RecurrentItem recurrentItem = validateRecurrentItem(formResult, "recurrentItemId", recurrentItemId);

        if (!formResult.isSuccess()) {
            return formResult;
        }

        // Delete
        recurrentItemDao.delete(recurrentItem);

        return formResult;
    }

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
    public FormResult update(String userId, long recurrentItemId, CreateOrUpdateRecurrentItemForm form) {

        FormResult formResult = new FormResult();

        // Validation
        entitlementService.canUpdateRecurrentItemOrFail(userId);
        validateMandatory(formResult, "clientShortName", form.getClientShortName());
        validateMandatory(formResult, "nextGenerationDate", form.getNextGenerationDate());
        validateDateOnly(formResult, "nextGenerationDate", form.getNextGenerationDate());
        validateMandatory(formResult, "description", form.getDescription());
        validateMandatory(formResult, "category", form.getCategory());
        RecurrentItem recurrentItem = validateRecurrentItem(formResult, "recurrentItemId", recurrentItemId);
        Client client = validateClientByShortName(formResult, "clientShortName", form.getClientShortName());

        if (!formResult.isSuccess()) {
            return formResult;
        }

        // Update
        new BeanPropertiesCopierTools(form, recurrentItem).copyAllSameProperties();
        recurrentItem.setClient(client);
        recurrentItem.setNextGenerationDate(DateTools.parseDateOnly(form.getNextGenerationDate()));
        recurrentItemDao.save(recurrentItem);

        return formResult;
    }

}
