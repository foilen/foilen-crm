package com.foilen.crm.services;

import com.foilen.crm.db.entities.user.User;
import com.foilen.crm.localonly.FakeDataServiceImpl;
import com.foilen.crm.test.AbstractSpringTests;
import com.foilen.crm.web.model.UpdateUserAdminForm;
import com.foilen.crm.web.model.UserList;
import com.foilen.smalltools.restapi.model.FormResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("User Service Implementation Tests")
public class UserServiceImplTest extends AbstractSpringTests {

    @Autowired
    private UserService userService;

    public UserServiceImplTest() {
        super(true);
    }

    @Nested
    @DisplayName("List Users Tests")
    class ListUsersTests {

        @Test
        @DisplayName("Non-admin users cannot list all users")
        void testListAll_notAdmin_FAIL() {
            expectNotAdmin(() -> userService.listAll(FakeDataServiceImpl.USER_ID_USER, 1, null));
        }

        @Test
        @DisplayName("Admin users can list all users")
        void testListAll_OK() {
            UserList result = userService.listAll(FakeDataServiceImpl.USER_ID_ADMIN, 1, null);
            assertEquals(4, result.getItems().size());
        }

        @Test
        @DisplayName("Admin users can search users by user id")
        void testListAll_search_OK() {
            UserList result = userService.listAll(FakeDataServiceImpl.USER_ID_ADMIN, 1, FakeDataServiceImpl.USER_ID_ADMIN);
            assertEquals(1, result.getItems().size());
            assertEquals(FakeDataServiceImpl.USER_ID_ADMIN, result.getItems().get(0).getUserId());
        }
    }

    @Nested
    @DisplayName("Update User Admin Tests")
    class UpdateUserAdminTests {

        @Test
        @DisplayName("Non-admin users cannot update the admin status of a user")
        void testUpdateAdmin_notAdmin_FAIL() {
            User targetUser = userRepository.findByUserId(FakeDataServiceImpl.USER_ID_TEST_1);

            expectNotAdmin(() -> userService.updateAdmin(FakeDataServiceImpl.USER_ID_USER, targetUser.getId(),
                    new UpdateUserAdminForm().setAdmin(true)));

            assertFalse(userRepository.findByUserId(FakeDataServiceImpl.USER_ID_TEST_1).isAdmin());
        }

        @Test
        @DisplayName("Cannot update the admin status of a user that does not exist")
        void testUpdateAdmin_userNotExist_FAIL() {
            FormResult result = userService.updateAdmin(FakeDataServiceImpl.USER_ID_ADMIN, "does-not-exist",
                    new UpdateUserAdminForm().setAdmin(true));

            assertFalse(result.isSuccess());
            assertTrue(result.getValidationErrorsByField().get("id").contains("error.userNotExist"));
        }

        @Test
        @DisplayName("Admin users can grant admin rights to another user")
        void testUpdateAdmin_grant_OK() {
            User targetUser = userRepository.findByUserId(FakeDataServiceImpl.USER_ID_TEST_1);

            FormResult result = userService.updateAdmin(FakeDataServiceImpl.USER_ID_ADMIN, targetUser.getId(),
                    new UpdateUserAdminForm().setAdmin(true));

            assertTrue(result.isSuccess());
            assertTrue(userRepository.findByUserId(FakeDataServiceImpl.USER_ID_TEST_1).isAdmin());
        }

        @Test
        @DisplayName("Admin users can revoke admin rights from another user")
        void testUpdateAdmin_revoke_OK() {
            User targetUser = userRepository.findByUserId(FakeDataServiceImpl.USER_ID_TEST_1);
            userService.updateAdmin(FakeDataServiceImpl.USER_ID_ADMIN, targetUser.getId(), new UpdateUserAdminForm().setAdmin(true));

            FormResult result = userService.updateAdmin(FakeDataServiceImpl.USER_ID_ADMIN, targetUser.getId(),
                    new UpdateUserAdminForm().setAdmin(false));

            assertTrue(result.isSuccess());
            assertFalse(userRepository.findByUserId(FakeDataServiceImpl.USER_ID_TEST_1).isAdmin());
        }

        @Test
        @DisplayName("Admin users cannot remove their own admin rights")
        void testUpdateAdmin_cannotRemoveOwn_FAIL() {
            User adminUser = userRepository.findByUserId(FakeDataServiceImpl.USER_ID_ADMIN);

            FormResult result = userService.updateAdmin(FakeDataServiceImpl.USER_ID_ADMIN, adminUser.getId(),
                    new UpdateUserAdminForm().setAdmin(false));

            assertFalse(result.isSuccess());
            assertTrue(result.getGlobalErrors().contains("error.cannotRemoveOwnAdmin"));
            assertTrue(userRepository.findByUserId(FakeDataServiceImpl.USER_ID_ADMIN).isAdmin());
        }
    }

}
