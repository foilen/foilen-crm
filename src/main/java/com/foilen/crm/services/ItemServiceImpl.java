package com.foilen.crm.services;

import com.foilen.crm.db.repository.ItemRepository;
import com.foilen.crm.db.entities.invoice.Client;
import com.foilen.crm.db.entities.invoice.Item;
import com.foilen.crm.db.entities.invoice.TechnicalSupport;
import com.foilen.crm.db.entities.invoice.Transaction;
import com.foilen.crm.web.model.BillSomePendingItems;
import com.foilen.crm.web.model.CreateItemWithTime;
import com.foilen.crm.web.model.CreateOrUpdateItem;
import com.foilen.crm.web.model.ItemList;
import com.foilen.smalltools.reflection.BeanPropertiesCopierTools;
import com.foilen.smalltools.restapi.model.FormResult;
import com.foilen.smalltools.tools.CollectionsTools;
import com.foilen.smalltools.tools.DateTools;
import com.foilen.smalltools.tools.JsonTools;
import com.foilen.smalltools.tools.TimeConverterTools;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
@Transactional
public class ItemServiceImpl extends AbstractApiService implements ItemService {

    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private TransactionService transactionService;

    @Override
    public FormResult billPending(String userId, String invoicePrefix) {

        FormResult formResult = new FormResult();

        // Validation
        entitlementService.canBillItemOrFail(userId);
        validateMandatory(formResult, "invoicePrefix", invoicePrefix);

        if (!formResult.isSuccess()) {
            return formResult;
        }

        // Per client
        AtomicLong invoiceSuffix = new AtomicLong(1);
        List<String> clientIds = itemRepository.findAllDistinctClientIdByInvoiceIdNull();
        List<Client> clients = clientRepository.findAllById(clientIds).stream()
                .sorted(Comparator.comparing(Client::getShortName))
                .collect(Collectors.toList());
        List<Transaction> newTransactions = new ArrayList<>();
        for (Client client : clients) {
            logger.info("Processing client {}", client);

            // Create a transaction
            List<Item> items = itemRepository.findAllByInvoiceIdIsNullAndClientIdOrderByDateAscDescriptionAsc(client.getId());
            logger.info("Client {} has {} pending items", client, items.size());
            if (items.isEmpty()) {
                continue;
            }

            Transaction transaction = transactionService.createTransaction(client, items, invoicePrefix, invoiceSuffix);
            newTransactions.add(transaction);
        }

        // Send emails
        newTransactions.forEach(transaction -> transactionService.sendInvoice(transaction));

        return formResult;
    }

    @Override
    public FormResult billSomePending(String userId, BillSomePendingItems form) {

        FormResult formResult = new FormResult();

        // Validation
        entitlementService.canBillItemOrFail(userId);
        validateMandatory(formResult, "invoicePrefix", form.getInvoicePrefix());

        // Get all items
        List<Item> itemsToBill = itemRepository.findAllById(form.getItemToBillIds());

        // Ensure all found
        if (itemsToBill.size() != form.getItemToBillIds().size()) {
            CollectionsTools.getOrCreateEmptyArrayList(formResult.getValidationErrorsByField(), "itemToBillIds", String.class).add("error.mandatory");
        }

        // Ensure all not billed
        if (itemsToBill.stream().anyMatch(it -> it.getInvoiceId() != null)) {
            CollectionsTools.getOrCreateEmptyArrayList(formResult.getValidationErrorsByField(), "itemToBillIds", String.class).add("error.someAlreadyBilled");
        }

        if (!formResult.isSuccess()) {
            return formResult;
        }

        // Put per client
        Map<String, List<Item>> itemsPerClientId = itemsToBill.stream().collect(Collectors.groupingBy(Item::getClientId));
        List<Client> clients = clientRepository.findAllById(itemsPerClientId.keySet()).stream()
                .sorted(Comparator.comparing(Client::getShortName))
                .collect(Collectors.toList());

        // Per client
        AtomicLong invoiceSuffix = new AtomicLong(1);
        List<Transaction> newTransactions = new ArrayList<>();
        for (Client client : clients) {
            logger.info("Processing client {}", client);

            // Create a transaction
            List<Item> items = itemsPerClientId.get(client.getId());
            logger.info("Client {} has {} pending items", client, items.size());
            if (items.isEmpty()) {
                continue;
            }

            Transaction transaction = transactionService.createTransaction(client, items, form.getInvoicePrefix(), invoiceSuffix);
            newTransactions.add(transaction);
        }

        // Send emails
        newTransactions.forEach(transaction -> transactionService.sendInvoice(transaction));

        return formResult;
    }

    @Override
    public FormResult create(String userId, CreateItemWithTime form) {

        FormResult formResult = new FormResult();

        // Validation
        entitlementService.canCreateItemOrFail(userId);
        validateMandatory(formResult, "clientShortName", form.getClientShortName());
        validateDateOnly(formResult, "date", form.getDate());
        validateMandatory(formResult, "date", form.getDate());
        validateMandatory(formResult, "description", form.getDescription());
        validateMandatory(formResult, "category", form.getCategory());
        Client client = validateClientByShortName(formResult, "clientShortName", form.getClientShortName());
        TechnicalSupport technicalSupport = validateTechnicalSupportByClient(formResult, "clientShortName", client);

        if (!formResult.isSuccess()) {
            return formResult;
        }

        // Create
        Item entity = JsonTools.clone(form, Item.class);
        entity.setClientId(client.getId());

        // Calculate time and price
        long minutes = form.getHours() * 60 + form.getMinutes();
        double price = minutes / 60d * technicalSupport.getPricePerHourInCents();
        entity.setPriceInCents(Math.round(price));

        entity.setDescription(entity.getDescription() + " (" + TimeConverterTools.convertToTextFromMin(minutes) + ")");

        itemRepository.save(entity);

        return formResult;

    }

    @Override
    public FormResult create(String userId, CreateOrUpdateItem form) {
        FormResult formResult = new FormResult();

        // Validation
        entitlementService.canCreateItemOrFail(userId);
        validateMandatory(formResult, "clientShortName", form.getClientShortName());
        validateDateOnly(formResult, "date", form.getDate());
        validateMandatory(formResult, "date", form.getDate());
        validateMandatory(formResult, "description", form.getDescription());
        validateMandatory(formResult, "category", form.getCategory());
        Client client = validateClientByShortName(formResult, "clientShortName", form.getClientShortName());

        if (!formResult.isSuccess()) {
            return formResult;
        }

        // Create
        Item entity = JsonTools.clone(form, Item.class);
        entity.setClientId(client.getId());
        itemRepository.save(entity);

        return formResult;

    }

    @Override
    public FormResult delete(String userId, String id) {
        FormResult formResult = new FormResult();

        // Validation
        entitlementService.canDeleteItemOrFail(userId);
        Item item = validateItemById(formResult, "id", id);
        validateItemIsPending(formResult, "id", item);

        if (!formResult.isSuccess()) {
            return formResult;
        }

        // Delete
        itemRepository.delete(item);

        return formResult;
    }

    @Override
    public ItemList listBilled(String userId, int pageId) {

        // Validation
        validatePageId(pageId);
        entitlementService.canViewItemAllOrFail(userId);
        var clientIdFilter = ownedClientIdsOrNullIfAdmin(userId);

        // Retrieve
        ItemList result = new ItemList();
        Page<Item> page = itemRepository.findAllBilledSortedByClientName(PageRequest.of(pageId - 1, paginationService.getItemsPerPage()), clientIdFilter);
        paginationService.wrap(result, page, com.foilen.crm.web.model.Item.class);
        enrichClients(page, result.getItems());
        return result;
    }

    @Override
    public List<String> listDistinctCategories() {
        return itemRepository.findAllDistinctCategories();
    }

    @Override
    public ItemList listPending(String userId, int pageId) {

        // Validation
        validatePageId(pageId);
        entitlementService.canViewItemAllOrFail(userId);
        var clientIdFilter = ownedClientIdsOrNullIfAdmin(userId);

        // Retrieve
        ItemList result = new ItemList();
        Page<Item> page = itemRepository.findAllPendingSortedByClientName(PageRequest.of(pageId - 1, paginationService.getItemsPerPage()), clientIdFilter);
        paginationService.wrap(result, page, com.foilen.crm.web.model.Item.class);
        enrichClients(page, result.getItems());
        return result;
    }

    /**
     * Resolves each item's clientId into a {@link com.foilen.crm.web.model.ClientShort} on its API model,
     * since the Item document only stores a clientId reference.
     */
    private void enrichClients(Page<Item> page, List<com.foilen.crm.web.model.Item> items) {
        Map<String, com.foilen.crm.web.model.ClientShort> clientShorts = clientShortsByIds(page.getContent().stream()
                .map(Item::getClientId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet()));
        for (int i = 0; i < items.size(); i++) {
            items.get(i).setClient(clientShorts.get(page.getContent().get(i).getClientId()));
        }
    }

    @Override
    public FormResult update(String userId, String id, CreateOrUpdateItem form) {
        FormResult formResult = new FormResult();

        // Validation
        entitlementService.canUpdateItemOrFail(userId);
        validateMandatory(formResult, "clientShortName", form.getClientShortName());
        validateDateOnly(formResult, "date", form.getDate());
        validateMandatory(formResult, "date", form.getDate());
        validateMandatory(formResult, "description", form.getDescription());
        validateMandatory(formResult, "category", form.getCategory());
        Client client = validateClientByShortName(formResult, "clientShortName", form.getClientShortName());
        Item item = validateItemById(formResult, "id", id);
        validateItemIsPending(formResult, "id", item);

        if (!formResult.isSuccess()) {
            return formResult;
        }

        // Update
        new BeanPropertiesCopierTools(form, item).copyAllSameProperties();
        item.setClientId(client.getId());
        item.setDate(DateTools.parseDateOnly(form.getDate()));

        itemRepository.save(item);

        return formResult;
    }

}
