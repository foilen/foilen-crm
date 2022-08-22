/*
    Foilen CRM
    https://github.com/foilen/foilen-crm
    Copyright (c) 2015-2022 Foilen (https://foilen.com)

    The MIT License
    http://opensource.org/licenses/MIT

 */
package com.foilen.crm.test;

import org.junit.Assert;

import com.foilen.login.spring.client.security.FoilenLoginUserDetails;
import com.foilen.login.spring.services.FoilenLoginService;

public class FoilenLoginServiceMock implements FoilenLoginService {

    @Override
    public FoilenLoginUserDetails getLoggedInUserDetails() {
        Assert.fail("Not supposed to be used");
        return null;
    }

}
