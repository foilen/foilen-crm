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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

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
        entity.setClientId(client.getId());
        entity.setNextGenerationDate(DateTools.parseDateOnly(form.getNextGenerationDate()));
        recurrentItemRepository.save(entity);

        return formResult;

    }

    @Override
    public FormResult delete(String userId, String recurrentItemId) {

        FormResult formResult = new FormResult();

        // Validation
        entitlementService.canDeleteRecurrentItemOrFail(userId);
        RecurrentItem recurrentItem = validateRecurrentItem(formResult, "recurrentItemId", recurrentItemId);

        if (!formResult.isSuccess()) {
            return formResult;
        }

        // Delete
        recurrentItemRepository.delete(recurrentItem);

        return formResult;
    }

    @Override
    public void generateReady(Date now) {
        List<RecurrentItem> recurrentItems = recurrentItemRepository.findAllByNextGenerationDateLessThanEqual(now);
        recurrentItems.forEach(recurrentItem -> {
            logger.info("Creating {} that was due on {}", recurrentItem, DateTools.formatFull(recurrentItem.getNextGenerationDate()));

            // Create Item
            itemRepository.save(new Item(recurrentItem.getClientId(), null, now, recurrentItem.getDescription(), recurrentItem.getPriceInCents(), recurrentItem.getCategory()));

            // Update Recurrent Item
            recurrentItem.setNextGenerationDate(DateTools.addDate(recurrentItem.getNextGenerationDate(), recurrentItem.getCalendarUnit(), recurrentItem.getDelta()));
            recurrentItemRepository.save(recurrentItem);

        });
    }

    @Override
    public RecurrentItemList listAll(String userId, int pageId) {

        // Validation
        validatePageId(pageId);
        entitlementService.canViewRecurrentItemOrFail(userId);
        var clientIdFilter = ownedClientIdsOrNullIfAdmin(userId);

        // Retrieve
        RecurrentItemList result = new RecurrentItemList();
        Page<RecurrentItem> page = recurrentItemRepository.findAllSortedByClientName(PageRequest.of(pageId - 1, paginationService.getItemsPerPage()), clientIdFilter);
        paginationService.wrap(result, page, com.foilen.crm.web.model.RecurrentItem.class);

        // Resolve the clientId reference on each item
        Map<String, com.foilen.crm.web.model.ClientShort> clientShorts = clientShortsByIds(page.getContent().stream()
                .map(RecurrentItem::getClientId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet()));
        List<com.foilen.crm.web.model.RecurrentItem> items = result.getItems();
        for (int i = 0; i < items.size(); i++) {
            items.get(i).setClient(clientShorts.get(page.getContent().get(i).getClientId()));
        }

        return result;
    }

    @Override
    public FormResult update(String userId, String recurrentItemId, CreateOrUpdateRecurrentItemForm form) {

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
        recurrentItem.setClientId(client.getId());
        recurrentItem.setNextGenerationDate(DateTools.parseDateOnly(form.getNextGenerationDate()));
        recurrentItemRepository.save(recurrentItem);

        return formResult;
    }

}
