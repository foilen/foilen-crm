/*
    Foilen CRM
    https://github.com/foilen/foilen-crm
    Copyright (c) 2015-2022 Foilen (https://foilen.com)

    The MIT License
    http://opensource.org/licenses/MIT

 */
package com.foilen.crm.services;

import com.foilen.crm.db.entities.user.User;
import org.springframework.security.core.Authentication;

public interface EntitlementService {

    void canBillItemOrFail(String userId);

    void canCreateClientOrFail(String userId);

    void canCreateItemOrFail(String userId);

    void canCreatePaymentOrFail(String userId);

    void canCreateRecurrentItemOrFail(String userId);

    void canCreateTechnicalSupportOrFail(String userId);

    void canDeleteClientOrFail(String userId);

    void canDeleteItemOrFail(String userId);

    void canDeleteRecurrentItemOrFail(String userId);

    void canDeleteTechnicalSupportOrFail(String userId);

    void canUpdateClientOrFail(String userId);

    void canUpdateItemOrFail(String userId);

    void canUpdateRecurrentItemOrFail(String userId);

    void canUpdateTechnicalSupportOrFail(String userId);

    void canViewClientOrFail(String userId);

    void canViewItemAllOrFail(String userId);

    void canViewRecurrentItemOrFail(String userId);

    void canViewReportsOrFail(String userId);

    void canViewTechnicalSupportOrFail(String userId);

    void canViewTransactionOrFail(String userId);

    User getUserOrFail(Authentication authentication);
}
