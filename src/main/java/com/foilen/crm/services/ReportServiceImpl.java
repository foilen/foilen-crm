package com.foilen.crm.services;

import com.foilen.crm.db.repository.ItemRepository;
import com.foilen.crm.db.repository.TransactionRepository;
import com.foilen.crm.web.model.ReportBalanceByClient;
import com.foilen.crm.web.model.Reports;
import com.foilen.crm.web.model.ReportsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ReportServiceImpl extends AbstractApiService implements ReportService {

    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private TransactionRepository transactionRepository;

    @Override
    public ReportsResponse getReports(String userId) {

        // Validation
        entitlementService.canViewReportsOrFail(userId);

        // Retrieve
        ReportsResponse result = new ReportsResponse();
        Reports reports = new Reports();
        result.setItem(reports);

        reports.setItemsByCategory(itemRepository.findAllItemsByCategory());
        reports.setBalanceByClient(transactionRepository.findAllClientBalance());

        Map<String, Long> pendingTotalsByClientId = itemRepository.findAllPendingTotalsByClientId();
        reports.getBalanceByClient().forEach(balanceByClient ->
                balanceByClient.setPendingTotal(pendingTotalsByClientId.getOrDefault(balanceByClient.getClientId(), 0L)));

        reports.setGlobalBalance(reports.getBalanceByClient().stream()
                .mapToLong(ReportBalanceByClient::getTotal)
                .sum()
        );

        return result;
    }

}
