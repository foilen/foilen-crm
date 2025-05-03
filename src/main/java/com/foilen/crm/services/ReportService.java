package com.foilen.crm.services;

import com.foilen.crm.web.model.ReportsResponse;

public interface ReportService {

    /**
     * Get the reports.
     *
     * @param userId
     *            the user that wants the reports
     * @return the reports
     */
    ReportsResponse getReports(String userId);

}
