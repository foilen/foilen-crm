package com.foilen.crm.services;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.foilen.crm.localonly.FakeDataServiceImpl;
import com.foilen.crm.test.AbstractSpringTests;
import com.foilen.smalltools.test.asserts.AssertTools;

@DisplayName("Report Service Implementation Tests")
public class ReportServiceImplTest extends AbstractSpringTests {

    @Autowired
    private ReportService reportService;

    public ReportServiceImplTest() {
        super(true);
    }

    @Test
    @DisplayName("Admin users can get reports")
    void getReportsSucceeds() {
        AssertTools.assertJsonComparison("ReportServiceImplTest-testGetReports.json", getClass(), 
                reportService.getReports(FakeDataServiceImpl.USER_ID_ADMIN));
    }

}
