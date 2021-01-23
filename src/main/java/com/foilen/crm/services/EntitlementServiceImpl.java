/*
    Foilen CRM
    https://github.com/foilen/foilen-crm
    Copyright (c) 2015-2021 Foilen (https://foilen.com)

    The MIT License
    http://opensource.org/licenses/MIT

 */
package com.foilen.crm.services;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.foilen.crm.db.dao.UserDao;
import com.foilen.crm.db.entities.user.User;
import com.foilen.crm.exception.ErrorMessageException;
import com.foilen.login.spring.client.security.FoilenLoginUserDetails;
import com.foilen.login.spring.services.FoilenLoginService;
import com.foilen.smalltools.tools.AbstractBasics;

@Service
@Transactional
public class EntitlementServiceImpl extends AbstractBasics implements EntitlementService {

    @Autowired
    private FoilenLoginService foilenLoginService;
    @Autowired
    private UserDao userDao;

    private boolean isFirstUser = false;

    @Override
    public void canBillItemOrFail(String userId) {
        isAdminOrFail(userId);
    }

    @Override
    public void canCreateClientOrFail(String userId) {
        isAdminOrFail(userId);
    }

    @Override
    public void canCreateItemOrFail(String userId) {
        isAdminOrFail(userId);
    }

    @Override
    public void canCreatePaymentOrFail(String userId) {
        isAdminOrFail(userId);
    }

    @Override
    public void canViewClientOrFail(String userId) {
        isAdminOrFail(userId);
    }

    @Override
    public void canViewItemAllOrFail(String userId) {
        isAdminOrFail(userId);
    }

    @Override
    public void canViewRecurrentItemOrFail(String userId) {
        isAdminOrFail(userId);
    }

    @Override
    public void canViewReportsOrFail(String userId) {
        isAdminOrFail(userId);
    }

    @Override
    public void canViewTechnicalSupportOrFail(String userId) {
        isAdminOrFail(userId);
    }

    @Override
    public void canViewTransactionOrFail(String userId) {
        isAdminOrFail(userId);
    }

    private User getOrCreateUser(String userId) {
        User user = userDao.findByUserId(userId);
        if (user == null) {
            logger.info("User {} is unknown. Creating it. As admin? {}", isFirstUser);
            FoilenLoginUserDetails loginUserDetails = foilenLoginService.getLoggedInUserDetails();
            user = new User(loginUserDetails.getUsername(), isFirstUser);
            user.setEmail(loginUserDetails.getEmail());
            isFirstUser = false;
            userDao.save(user);
        }
        return user;
    }

    @PostConstruct
    public void init() {
        isFirstUser = userDao.count() == 0;
        logger.info("isFirstUser? {}", isFirstUser);
    }

    protected boolean isAdmin(String userId) {
        User user = getOrCreateUser(userId);
        return user.isAdmin();
    }

    private void isAdminOrFail(String userId) {
        if (!isAdmin(userId)) {
            throw new ErrorMessageException("error.notAdmin");
        }
    }

}
