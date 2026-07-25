package com.foilen.crm.web.controller;

import com.foilen.crm.services.AdminService;
import com.foilen.crm.web.model.ExportModel;
import com.foilen.crm.web.model.AdminExportResult;
import com.foilen.smalltools.restapi.model.FormResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping(value = "api/admin", produces = MimeTypeUtils.APPLICATION_JSON_VALUE)
@RestController
@SwaggerExpose
public class AdminApiController {

    @Autowired
    private AdminService adminService;

    @GetMapping("export")
    public AdminExportResult export(Authentication authentication) {
        return adminService.exportAll(authentication.getName());
    }

    @PostMapping("import")
    public FormResult importAll(
            Authentication authentication,
            @RequestBody ExportModel exportModel
    ) {
        return adminService.importAll(authentication.getName(), exportModel);
    }

}
