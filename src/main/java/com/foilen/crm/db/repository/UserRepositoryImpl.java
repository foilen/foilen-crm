package com.foilen.crm.db.repository;

import com.foilen.crm.db.entities.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class UserRepositoryImpl extends AbstractRepositoryCustom implements UserRepositoryCustom {

    @Override
    public Page<User> findAllSearch(String search, Pageable pageable) {
        Pattern pattern = Pattern.compile(Pattern.quote(search), Pattern.CASE_INSENSITIVE);
        Criteria criteria = new Criteria().orOperator(
                Criteria.where("userId").regex(pattern),
                Criteria.where("email").regex(pattern)
        );
        return find(User.class, pageable, criteria);
    }

}
