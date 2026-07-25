package com.foilen.crm.db.repository;

import com.foilen.crm.db.entities.invoice.Client;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class ClientRepositoryImpl extends AbstractRepositoryCustom implements ClientRepositoryCustom {

    @Override
    public Page<Client> findAllSearch(String search, Pageable pageable) {
        Pattern pattern = Pattern.compile(Pattern.quote(search), Pattern.CASE_INSENSITIVE);
        Criteria criteria = new Criteria().orOperator(
                Criteria.where("name").regex(pattern),
                Criteria.where("shortName").regex(pattern),
                Criteria.where("email").regex(pattern),
                Criteria.where("contactName").regex(pattern)
        );
        return find(Client.class, pageable, criteria);
    }

}
