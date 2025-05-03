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
