package com.foilen.crm.services;

import com.foilen.crm.db.entities.user.User;
import com.foilen.crm.localonly.FakeDataServiceImpl;
import com.foilen.crm.test.AbstractSpringTests;
import com.foilen.crm.web.model.ChangePasswordForm;
import com.foilen.crm.web.model.LoginForm;
import com.foilen.crm.web.model.LoginWithCodeForm;
import com.foilen.crm.web.model.LoginWithCodeRequestForm;
import com.foilen.crm.web.model.UpdateUserAdminForm;
import com.foilen.crm.web.model.UpdateUserDisabledForm;
import com.foilen.crm.web.model.UserList;
import com.foilen.smalltools.restapi.model.FormResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("User Service Implementation Tests")
public class UserServiceImplTest extends AbstractSpringTests {

    @Autowired
    private PasswordEncoder passwordEncoder;
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
            assertEquals(FakeDataServiceImpl.USER_ID_ADMIN, result.getItems().get(0).getEmail());
        }
    }

    @Nested
    @DisplayName("Update User Admin Tests")
    class UpdateUserAdminTests {

        @Test
        @DisplayName("Non-admin users cannot update the admin status of a user")
        void testUpdateAdmin_notAdmin_FAIL() {
            User targetUser = userRepository.findByEmail(FakeDataServiceImpl.USER_ID_TEST_1);

            expectNotAdmin(() -> userService.updateAdmin(FakeDataServiceImpl.USER_ID_USER, targetUser.getEmail(),
                    new UpdateUserAdminForm().setAdmin(true)));

            assertFalse(userRepository.findByEmail(FakeDataServiceImpl.USER_ID_TEST_1).isAdmin());
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
            User targetUser = userRepository.findByEmail(FakeDataServiceImpl.USER_ID_TEST_1);

            FormResult result = userService.updateAdmin(FakeDataServiceImpl.USER_ID_ADMIN, targetUser.getEmail(),
                    new UpdateUserAdminForm().setAdmin(true));

            assertTrue(result.isSuccess());
            assertTrue(userRepository.findByEmail(FakeDataServiceImpl.USER_ID_TEST_1).isAdmin());
        }

        @Test
        @DisplayName("Admin users can revoke admin rights from another user")
        void testUpdateAdmin_revoke_OK() {
            User targetUser = userRepository.findByEmail(FakeDataServiceImpl.USER_ID_TEST_1);
            userService.updateAdmin(FakeDataServiceImpl.USER_ID_ADMIN, targetUser.getEmail(), new UpdateUserAdminForm().setAdmin(true));

            FormResult result = userService.updateAdmin(FakeDataServiceImpl.USER_ID_ADMIN, targetUser.getEmail(),
                    new UpdateUserAdminForm().setAdmin(false));

            assertTrue(result.isSuccess());
            assertFalse(userRepository.findByEmail(FakeDataServiceImpl.USER_ID_TEST_1).isAdmin());
        }

        @Test
        @DisplayName("Admin users cannot remove their own admin rights")
        void testUpdateAdmin_cannotRemoveOwn_FAIL() {
            User adminUser = userRepository.findByEmail(FakeDataServiceImpl.USER_ID_ADMIN);

            FormResult result = userService.updateAdmin(FakeDataServiceImpl.USER_ID_ADMIN, adminUser.getEmail(),
                    new UpdateUserAdminForm().setAdmin(false));

            assertFalse(result.isSuccess());
            assertTrue(result.getGlobalErrors().contains("error.cannotRemoveOwnAdmin"));
            assertTrue(userRepository.findByEmail(FakeDataServiceImpl.USER_ID_ADMIN).isAdmin());
        }
    }

    @Nested
    @DisplayName("Update User Disabled Tests")
    class UpdateUserDisabledTests {

        @Test
        @DisplayName("Non-admin users cannot disable a user")
        void testUpdateDisabled_notAdmin_FAIL() {
            User targetUser = userRepository.findByEmail(FakeDataServiceImpl.USER_ID_TEST_1);

            expectNotAdmin(() -> userService.updateDisabled(FakeDataServiceImpl.USER_ID_USER, targetUser.getEmail(),
                    new UpdateUserDisabledForm().setDisabled(true)));
        }

        @Test
        @DisplayName("Admin users can disable another user")
        void testUpdateDisabled_OK() {
            User targetUser = userRepository.findByEmail(FakeDataServiceImpl.USER_ID_TEST_1);

            FormResult result = userService.updateDisabled(FakeDataServiceImpl.USER_ID_ADMIN, targetUser.getEmail(),
                    new UpdateUserDisabledForm().setDisabled(true));

            assertTrue(result.isSuccess());
            assertTrue(userRepository.findByEmail(FakeDataServiceImpl.USER_ID_TEST_1).isDisabled());
        }

        @Test
        @DisplayName("Admin users cannot disable their own account")
        void testUpdateDisabled_cannotDisableOwn_FAIL() {
            User adminUser = userRepository.findByEmail(FakeDataServiceImpl.USER_ID_ADMIN);

            FormResult result = userService.updateDisabled(FakeDataServiceImpl.USER_ID_ADMIN, adminUser.getEmail(),
                    new UpdateUserDisabledForm().setDisabled(true));

            assertFalse(result.isSuccess());
            assertTrue(result.getGlobalErrors().contains("error.cannotDisableOwnAccount"));
            assertFalse(userRepository.findByEmail(FakeDataServiceImpl.USER_ID_ADMIN).isDisabled());
        }
    }

    @Nested
    @DisplayName("Login Tests")
    class LoginTests {

        @Test
        @DisplayName("Cannot login with an unknown email")
        void testLogin_unknownEmail_FAIL() {
            FormResult result = userService.login(new LoginForm().setEmail("unknown@example.com").setPassword("secret"),
                    new MockHttpServletRequest(), new MockHttpServletResponse());

            assertFalse(result.isSuccess());
            assertTrue(result.getGlobalErrors().contains("error.login"));
        }

        @Test
        @DisplayName("Cannot login when no password is set yet")
        void testLogin_noPasswordSet_FAIL() {
            FormResult result = userService.login(new LoginForm().setEmail(FakeDataServiceImpl.USER_ID_ADMIN).setPassword("secret"),
                    new MockHttpServletRequest(), new MockHttpServletResponse());

            assertFalse(result.isSuccess());
            assertTrue(result.getGlobalErrors().contains("error.login"));
        }

        @Test
        @DisplayName("Can login with the correct password")
        void testLogin_OK() {
            User user = userRepository.findByEmail(FakeDataServiceImpl.USER_ID_ADMIN);
            user.setPasswordHash(passwordEncoder.encode("secret"));
            userRepository.save(user);

            FormResult result = userService.login(new LoginForm().setEmail(FakeDataServiceImpl.USER_ID_ADMIN).setPassword("secret"),
                    new MockHttpServletRequest(), new MockHttpServletResponse());

            assertTrue(result.isSuccess());
            assertNotNull(userRepository.findByEmail(FakeDataServiceImpl.USER_ID_ADMIN).getLastLogin());
        }

        @Test
        @DisplayName("Cannot login with the wrong password")
        void testLogin_wrongPassword_FAIL() {
            User user = userRepository.findByEmail(FakeDataServiceImpl.USER_ID_ADMIN);
            user.setPasswordHash(passwordEncoder.encode("secret"));
            userRepository.save(user);

            FormResult result = userService.login(new LoginForm().setEmail(FakeDataServiceImpl.USER_ID_ADMIN).setPassword("wrong"),
                    new MockHttpServletRequest(), new MockHttpServletResponse());

            assertFalse(result.isSuccess());
            assertTrue(result.getGlobalErrors().contains("error.login"));
        }

        @Test
        @DisplayName("Cannot login when the account is disabled")
        void testLogin_disabled_FAIL() {
            User user = userRepository.findByEmail(FakeDataServiceImpl.USER_ID_ADMIN);
            user.setPasswordHash(passwordEncoder.encode("secret"));
            user.setDisabled(true);
            userRepository.save(user);

            FormResult result = userService.login(new LoginForm().setEmail(FakeDataServiceImpl.USER_ID_ADMIN).setPassword("secret"),
                    new MockHttpServletRequest(), new MockHttpServletResponse());

            assertFalse(result.isSuccess());
            assertTrue(result.getGlobalErrors().contains("error.userDisabled"));
        }
    }

    @Nested
    @DisplayName("Login With Code Tests")
    class LoginWithCodeTests {

        @Test
        @DisplayName("Requesting a code for a new email creates a non-admin user")
        void testLoginWithCodeRequest_newUser_OK() {
            FormResult result = userService.loginWithCodeRequest(new LoginWithCodeRequestForm().setEmail("brandnew1@example.com"));

            assertTrue(result.isSuccess());
            User user = userRepository.findByEmail("brandnew1@example.com");
            assertNotNull(user);
            assertFalse(user.isAdmin());
            assertNotNull(user.getLoginCode());
        }

        @Test
        @DisplayName("Requesting a code twice within a minute is rate limited")
        void testLoginWithCodeRequest_rateLimited_FAIL() {
            userService.loginWithCodeRequest(new LoginWithCodeRequestForm().setEmail("brandnew2@example.com"));

            FormResult result = userService.loginWithCodeRequest(new LoginWithCodeRequestForm().setEmail("brandnew2@example.com"));

            assertFalse(result.isSuccess());
            assertTrue(result.getGlobalErrors().contains("error.codeTooFrequent"));
        }

        @Test
        @DisplayName("Cannot request a code for a disabled account")
        void testLoginWithCodeRequest_disabled_FAIL() {
            User user = userRepository.findByEmail(FakeDataServiceImpl.USER_ID_ADMIN);
            user.setDisabled(true);
            userRepository.save(user);

            FormResult result = userService.loginWithCodeRequest(new LoginWithCodeRequestForm().setEmail(FakeDataServiceImpl.USER_ID_ADMIN));

            assertFalse(result.isSuccess());
            assertTrue(result.getGlobalErrors().contains("error.userDisabled"));
        }

        @Test
        @DisplayName("Can login with a valid code")
        void testLoginWithCode_OK() {
            userService.loginWithCodeRequest(new LoginWithCodeRequestForm().setEmail("brandnew3@example.com"));
            String code = userRepository.findByEmail("brandnew3@example.com").getLoginCode();

            FormResult result = userService.loginWithCode(new LoginWithCodeForm().setEmail("brandnew3@example.com").setCode(code),
                    new MockHttpServletRequest(), new MockHttpServletResponse());

            assertTrue(result.isSuccess());
            assertNull(userRepository.findByEmail("brandnew3@example.com").getLoginCode());
        }

        @Test
        @DisplayName("Cannot login with a wrong code")
        void testLoginWithCode_wrongCode_FAIL() {
            userService.loginWithCodeRequest(new LoginWithCodeRequestForm().setEmail("brandnew4@example.com"));

            FormResult result = userService.loginWithCode(new LoginWithCodeForm().setEmail("brandnew4@example.com").setCode("000000"),
                    new MockHttpServletRequest(), new MockHttpServletResponse());

            assertFalse(result.isSuccess());
            assertTrue(result.getGlobalErrors().contains("error.login"));
        }

        @Test
        @DisplayName("Cannot login with an expired code")
        void testLoginWithCode_expired_FAIL() {
            userService.loginWithCodeRequest(new LoginWithCodeRequestForm().setEmail("brandnew5@example.com"));
            User user = userRepository.findByEmail("brandnew5@example.com");
            String code = user.getLoginCode();
            user.setLoginCodeExpiration(new Date(System.currentTimeMillis() - 1000));
            userRepository.save(user);

            FormResult result = userService.loginWithCode(new LoginWithCodeForm().setEmail("brandnew5@example.com").setCode(code),
                    new MockHttpServletRequest(), new MockHttpServletResponse());

            assertFalse(result.isSuccess());
            assertTrue(result.getGlobalErrors().contains("error.expired"));
        }
    }

    @Nested
    @DisplayName("Change Password Tests")
    class ChangePasswordTests {

        @Test
        @DisplayName("Can set a password for the first time without a current password")
        void testChangePassword_firstTime_OK() {
            FormResult result = userService.changePassword(FakeDataServiceImpl.USER_ID_ADMIN,
                    new ChangePasswordForm().setNewPassword("secret123").setNewPasswordConfirmation("secret123"));

            assertTrue(result.isSuccess());
            User user = userRepository.findByEmail(FakeDataServiceImpl.USER_ID_ADMIN);
            assertTrue(passwordEncoder.matches("secret123", user.getPasswordHash()));
        }

        @Test
        @DisplayName("New password and confirmation must match")
        void testChangePassword_mismatch_FAIL() {
            FormResult result = userService.changePassword(FakeDataServiceImpl.USER_ID_ADMIN,
                    new ChangePasswordForm().setNewPassword("secret123").setNewPasswordConfirmation("other"));

            assertFalse(result.isSuccess());
        }

        @Test
        @DisplayName("Must provide the correct current password once one is already set")
        void testChangePassword_requiresCurrent_FAIL() {
            User user = userRepository.findByEmail(FakeDataServiceImpl.USER_ID_ADMIN);
            user.setPasswordHash(passwordEncoder.encode("oldSecret"));
            userRepository.save(user);

            FormResult result = userService.changePassword(FakeDataServiceImpl.USER_ID_ADMIN,
                    new ChangePasswordForm().setNewPassword("secret123").setNewPasswordConfirmation("secret123"));

            assertFalse(result.isSuccess());
        }

        @Test
        @DisplayName("Can change the password with the correct current password")
        void testChangePassword_OK() {
            User user = userRepository.findByEmail(FakeDataServiceImpl.USER_ID_ADMIN);
            user.setPasswordHash(passwordEncoder.encode("oldSecret"));
            userRepository.save(user);

            FormResult result = userService.changePassword(FakeDataServiceImpl.USER_ID_ADMIN,
                    new ChangePasswordForm().setCurrentPassword("oldSecret").setNewPassword("secret123").setNewPasswordConfirmation("secret123"));

            assertTrue(result.isSuccess());
            assertTrue(passwordEncoder.matches("secret123", userRepository.findByEmail(FakeDataServiceImpl.USER_ID_ADMIN).getPasswordHash()));
        }
    }

}
