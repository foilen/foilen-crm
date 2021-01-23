/*
    Foilen CRM
    https://github.com/foilen/foilen-crm
    Copyright (c) 2015-2021 Foilen (https://foilen.com)

    The MIT License
    http://opensource.org/licenses/MIT

 */
package com.foilen.crm.services;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.foilen.crm.localonly.FakeDataServiceImpl;
import com.foilen.crm.test.AbstractSpringTests;
import com.foilen.smalltools.test.asserts.AssertTools;

public class ReportServiceImplTest extends AbstractSpringTests {

    @Autowired
    private ReportService reportService;

    public ReportServiceImplTest() {
        super(true);
    }

    @Test
    public void testGetReports() {

        AssertTools.assertJsonComparison("ReportServiceImplTest-testGetReports.json", getClass(), reportService.getReports(FakeDataServiceImpl.USER_ID_ADMIN));

    }

}
