/*
    Foilen CRM
    https://github.com/foilen/foilen-crm
    Copyright (c) 2015-2020 Foilen (http://foilen.com)

    The MIT License
    http://opensource.org/licenses/MIT

 */
package com.foilen.crm.services;

import org.springframework.data.domain.Page;

import com.foilen.smalltools.restapi.model.AbstractListResultWithPagination;

public interface PaginationService {

    int getItemsPerPage();

    <T> void wrap(AbstractListResultWithPagination<T> results, Page<?> page, Class<T> apiType);

}
