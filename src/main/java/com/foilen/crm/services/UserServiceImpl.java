package com.foilen.crm.services;

import com.foilen.crm.db.entities.user.User;
import com.foilen.crm.web.model.UpdateUserAdminForm;
import com.foilen.crm.web.model.UserList;
import com.foilen.smalltools.restapi.model.FormResult;
import com.foilen.smalltools.tools.StringTools;
import com.google.common.base.Strings;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class UserServiceImpl extends AbstractApiService implements UserService {

    @Override
    public UserList listAll(String userId, int pageId, String search) {

        // Validation
        validatePageId(pageId);
        entitlementService.canManageUsersOrFail(userId);

        if (Strings.isNullOrEmpty(search)) {
            search = null;
        }

        // Retrieve
        UserList result = new UserList();
        Page<User> page;
        if (search == null) {
            page = userDao.findAll(PageRequest.of(pageId - 1, paginationService.getItemsPerPage(), Direction.ASC, "email"));
        } else {
            search = "%" + search + "%";
            page = userDao.findAllSearch(search, PageRequest.of(pageId - 1, paginationService.getItemsPerPage(), Direction.ASC, "email"));
        }
        paginationService.wrap(result, page, com.foilen.crm.web.model.User.class);
        return result;
    }

    @Override
    public FormResult updateAdmin(String userId, Long id, UpdateUserAdminForm form) {

        FormResult formResult = new FormResult();

        // Validation
        entitlementService.canManageUsersOrFail(userId);
        User user = validateUserById(formResult, "id", id);

        if (!formResult.isSuccess()) {
            return formResult;
        }

        if (!form.isAdmin() && StringTools.safeEquals(userId, user.getUserId())) {
            formResult.getGlobalErrors().add("error.cannotRemoveOwnAdmin");
            return formResult;
        }

        // Update
        user.setAdmin(form.isAdmin());
        userDao.save(user);

        return formResult;
    }

}
