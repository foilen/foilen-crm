package com.foilen.crm.db.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.foilen.crm.db.entities.user.User;

@Repository
public interface UserDao extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u WHERE u.userId LIKE :search OR" +
            " u.email LIKE :search"
    )
    Page<User> findAllSearch(@Param("search") String search, Pageable pageable);

    User findByUserId(String userId);

}
