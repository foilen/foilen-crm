package com.foilen.crm.db.repository;

import com.foilen.crm.db.entities.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserRepositoryCustom {

    Page<User> findAllSearch(String search, Pageable pageable);

}
