package com.foilen.crm.services;

import com.foilen.crm.web.model.ApplicationDetailsResult;

public interface ApplicationService {

    ApplicationDetailsResult getDetails(String userId);

}
