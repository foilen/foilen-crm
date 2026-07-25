package com.foilen.crm.db.repository;

import com.foilen.crm.db.entities.invoice.TechnicalSupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class TechnicalSupportRepositoryImpl extends AbstractRepositoryCustom implements TechnicalSupportRepositoryCustom {

    @Override
    public Page<TechnicalSupport> findAllSearch(String search, Pageable pageable) {
        Pattern pattern = Pattern.compile(Pattern.quote(search), Pattern.CASE_INSENSITIVE);
        Criteria criteria = Criteria.where("sid").regex(pattern);
        return find(TechnicalSupport.class, pageable, criteria);
    }

}
