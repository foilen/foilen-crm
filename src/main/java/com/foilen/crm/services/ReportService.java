/*
    Foilen CRM
    https://github.com/foilen/foilen-crm
    Copyright (c) 2015-2021 Foilen (https://foilen.com)

    The MIT License
    http://opensource.org/licenses/MIT

 */
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
