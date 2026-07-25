package com.foilen.crm.web.model;

import java.util.ArrayList;
import java.util.List;

import com.foilen.smalltools.restapi.model.AbstractApiBase;

public class ExportModel extends AbstractApiBase {

    private List<ExportTechnicalSupport> technicalSupports = new ArrayList<>();
    private List<ExportClient> clients = new ArrayList<>();
    private List<ExportItem> items = new ArrayList<>();
    private List<ExportRecurrentItem> recurrentItems = new ArrayList<>();
    private List<ExportTransaction> transactions = new ArrayList<>();
    private List<ExportUser> users = new ArrayList<>();

    public List<ExportClient> getClients() {
        return clients;
    }

    public List<ExportItem> getItems() {
        return items;
    }

    public List<ExportRecurrentItem> getRecurrentItems() {
        return recurrentItems;
    }

    public List<ExportTechnicalSupport> getTechnicalSupports() {
        return technicalSupports;
    }

    public List<ExportTransaction> getTransactions() {
        return transactions;
    }

    public List<ExportUser> getUsers() {
        return users;
    }

    public void setClients(List<ExportClient> clients) {
        this.clients = clients;
    }

    public void setItems(List<ExportItem> items) {
        this.items = items;
    }

    public void setRecurrentItems(List<ExportRecurrentItem> recurrentItems) {
        this.recurrentItems = recurrentItems;
    }

    public void setTechnicalSupports(List<ExportTechnicalSupport> technicalSupports) {
        this.technicalSupports = technicalSupports;
    }

    public void setTransactions(List<ExportTransaction> transactions) {
        this.transactions = transactions;
    }

    public void setUsers(List<ExportUser> users) {
        this.users = users;
    }

}
