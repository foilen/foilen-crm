package com.foilen.crm.services;

import org.springframework.data.domain.Page;

import com.foilen.smalltools.restapi.model.AbstractListResultWithPagination;

public interface PaginationService {

    int getItemsPerPage();

    <T> void wrap(AbstractListResultWithPagination<T> results, Page<?> page, Class<T> apiType);

}
