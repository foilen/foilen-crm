/*
    Foilen CRM
    https://github.com/foilen/foilen-crm
    Copyright (c) 2015-2021 Foilen (https://foilen.com)

    The MIT License
    http://opensource.org/licenses/MIT

 */
package com.foilen.crm.services;

public interface EntitlementService {

    void canBillItemOrFail(String userId);

    void canCreateClientOrFail(String userId);

    void canCreateItemOrFail(String userId);

    void canCreatePaymentOrFail(String userId);

    void canViewClientOrFail(String userId);

    void canViewItemAllOrFail(String userId);

    void canViewRecurrentItemOrFail(String userId);

    void canViewReportsOrFail(String userId);

    void canViewTechnicalSupportOrFail(String userId);

    void canViewTransactionOrFail(String userId);

}
