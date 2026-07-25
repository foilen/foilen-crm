package com.foilen.crm.services;

import com.foilen.crm.web.model.ExportModel;
import com.foilen.crm.web.model.AdminExportResult;
import com.foilen.smalltools.restapi.model.FormResult;

public interface AdminService {

    AdminExportResult exportAll(String userId);

    FormResult importAll(String userId, ExportModel exportModel);

}
