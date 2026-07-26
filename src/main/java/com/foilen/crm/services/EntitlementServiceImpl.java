package com.foilen.crm.services;

import com.foilen.crm.db.repository.UserRepository;
import com.foilen.crm.db.entities.user.User;
import com.foilen.crm.exception.ErrorMessageException;
import com.foilen.smalltools.tools.AbstractBasics;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class EntitlementServiceImpl extends AbstractBasics implements EntitlementService {

    @Autowired
    private UserRepository userRepository;

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
    public void canExportDataOrFail(String userId) {
        isAdminOrFail(userId);
    }

    @Override
    public void canImportDataOrFail(String userId) {
        isAdminOrFail(userId);
    }

    @Override
    public void canManageUsersOrFail(String userId) {
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
    public void canUpdatePaymentOrFail(String userId) {
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

    // Viewing a Client / Item / RecurrentItem / Transaction listing is open to any active user;
    // the service layer restricts non-admins to their own client's data.
    @Override
    public void canViewClientOrFail(String userId) {
        getUserOrFail(userId);
    }

    @Override
    public void canViewItemAllOrFail(String userId) {
        getUserOrFail(userId);
    }

    @Override
    public void canViewRecurrentItemOrFail(String userId) {
        getUserOrFail(userId);
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
        getUserOrFail(userId);
    }

    @Override
    public User getUserOrFail(Authentication authentication) {
        return getUserOrFail(authentication.getName());
    }

    @Override
    public User getUserOrFail(String userId) {
        User user = userRepository.findByEmail(userId);
        if (user == null) {
            throw new RuntimeException("User does not exist");
        }
        if (user.isDisabled()) {
            throw new ErrorMessageException("error.userDisabled");
        }
        return user;
    }

    @Override
    public boolean isAdmin(String userId) {
        return getUserOrFail(userId).isAdmin();
    }

    private void isAdminOrFail(String userId) {
        if (!isAdmin(userId)) {
            throw new ErrorMessageException("error.notAdmin");
        }
    }

}
