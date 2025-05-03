package com.foilen.crm.db.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.foilen.crm.db.entities.user.User;

@Repository
public interface UserDao extends JpaRepository<User, Long> {

    User findByUserId(String userId);

}
