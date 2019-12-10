/*
    Foilen CRM
    https://github.com/foilen/foilen-crm
    Copyright (c) 2015-2019 Foilen (http://foilen.com)

    The MIT License
    http://opensource.org/licenses/MIT

 */
package com.foilen.crm.services;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.foilen.crm.db.dao.ItemDao;
import com.foilen.crm.db.dao.TransactionDao;
import com.foilen.crm.web.model.ReportBalanceByClient;
import com.foilen.crm.web.model.Reports;
import com.foilen.crm.web.model.ReportsResponse;

@Service
public class ReportServiceImpl extends AbstractApiService implements ReportService {

    @Autowired
    private ItemDao itemDao;
    @Autowired
    private TransactionDao transactionDao;

    @Override
    public ReportsResponse getReports(String userId) {

        // Validation
        entitlementService.canViewReportsOrFail(userId);

        // Retrieve
        ReportsResponse result = new ReportsResponse();
        Reports reports = new Reports();
        result.setItem(reports);

        reports.setItemsByCategory(itemDao.findAllItemsByCategory());
        reports.setBalanceByClient(transactionDao.findAllClientBalance());

        reports.setGlobalBalance(reports.getBalanceByClient().stream() //
                .collect(Collectors.summingLong(ReportBalanceByClient::getTotal)) //
        );

        return result;
    }

}
