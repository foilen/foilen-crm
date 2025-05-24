package com.foilen.crm.services;

import com.foilen.crm.db.dao.ItemDao;
import com.foilen.crm.db.dao.TransactionDao;
import com.foilen.crm.web.model.ReportBalanceByClient;
import com.foilen.crm.web.model.Reports;
import com.foilen.crm.web.model.ReportsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

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

        reports.setGlobalBalance(reports.getBalanceByClient().stream()
                .collect(Collectors.summingLong(ReportBalanceByClient::getTotal))
        );

        return result;
    }

}
