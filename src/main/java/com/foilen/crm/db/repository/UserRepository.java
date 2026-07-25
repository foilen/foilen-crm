package com.foilen.crm.db.repository;

import com.foilen.crm.db.entities.user.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<User, String>, UserRepositoryCustom {

    User findByUserId(String userId);

}
