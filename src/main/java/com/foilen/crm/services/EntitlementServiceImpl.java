package com.foilen.crm.services;

import com.foilen.crm.db.dao.UserDao;
import com.foilen.crm.db.entities.user.User;
import com.foilen.crm.exception.ErrorMessageException;
import com.foilen.smalltools.tools.AbstractBasics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;

@Service
@Transactional
public class EntitlementServiceImpl extends AbstractBasics implements EntitlementService {

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
    public void canCreateRecurrentItemOrFail(String userId) {
        isAdminOrFail(userId);
    }

    @Override
    public void canCreateTechnicalSupportOrFail(String userId) {
        isAdminOrFail(userId);
    }

    @Override
    public void canDeleteClientOrFail(String userId) {
        isAdminOrFail(userId);
    }

    @Override
    public void canDeleteItemOrFail(String userId) {
        isAdminOrFail(userId);
    }

    @Override
    public void canDeleteRecurrentItemOrFail(String userId) {
        isAdminOrFail(userId);
    }

    @Override
    public void canDeleteTechnicalSupportOrFail(String userId) {
        isAdminOrFail(userId);
    }

    @Override
    public void canUpdateClientOrFail(String userId) {
        isAdminOrFail(userId);
    }

    @Override
    public void canUpdateItemOrFail(String userId) {
        isAdminOrFail(userId);
    }

    @Override
    public void canUpdateRecurrentItemOrFail(String userId) {
        isAdminOrFail(userId);
    }

    @Override
    public void canUpdateTechnicalSupportOrFail(String userId) {
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

    private User getUserOrFail(String userId) {
        User user = userDao.findByUserId(userId);
        if (user == null) {
            throw new RuntimeException("User does not exist");
        }
        return user;
    }

    @Override
    public User getUserOrFail(Authentication authentication) {
        var userId = authentication.getName();
        User user = userDao.findByUserId(userId);
        if (user == null) {
            if (authentication instanceof OAuth2AuthenticationToken) {
                var authenticationToken = (OAuth2AuthenticationToken) authentication;
                user = new User(userId, isFirstUser);
                user.setEmail(authenticationToken.getPrincipal().getAttribute("email"));
                isFirstUser = false;
                userDao.save(user);
            } else {
                throw new RuntimeException("Not OAuth2");
            }
        }

        return user;
    }

    @PostConstruct
    public void init() {
        isFirstUser = userDao.count() == 0;
        logger.info("isFirstUser? {}", isFirstUser);
    }

    protected boolean isAdmin(String userId) {
        User user = getUserOrFail(userId);
        return user.isAdmin();
    }

    private void isAdminOrFail(String userId) {
        if (!isAdmin(userId)) {
            throw new ErrorMessageException("error.notAdmin");
        }
    }

}
