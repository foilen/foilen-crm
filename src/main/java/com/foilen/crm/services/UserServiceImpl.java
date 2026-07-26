package com.foilen.crm.services;

import com.foilen.crm.db.entities.user.User;
import com.foilen.crm.web.model.ChangePasswordForm;
import com.foilen.crm.web.model.LoginForm;
import com.foilen.crm.web.model.LoginWithCodeForm;
import com.foilen.crm.web.model.LoginWithCodeRequestForm;
import com.foilen.crm.web.model.UpdateUserAdminForm;
import com.foilen.crm.web.model.UpdateUserDisabledForm;
import com.foilen.crm.web.model.UserList;
import com.foilen.smalltools.email.EmailBuilder;
import com.foilen.smalltools.email.EmailService;
import com.foilen.smalltools.restapi.model.FormResult;
import com.foilen.smalltools.tools.CollectionsTools;
import com.foilen.smalltools.tools.StringTools;
import com.google.common.base.Strings;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

@Service
@Transactional
public class UserServiceImpl extends AbstractApiService implements UserService {

    private static final long CODE_RATE_LIMIT_MILLIS = 60_000L; // 1 minute
    private static final long CODE_VALIDITY_MILLIS = 10 * 60_000L; // 10 minutes

    @Autowired
    private EmailService emailService;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private SecurityContextRepository securityContextRepository;

    @Value("${crm.mailFrom}")
    private String mailFrom;
    @Value("${crm.mailForceEmailTo:#{null}}")
    private String mailForceEmailTo;

    private void authenticate(User user, HttpServletRequest request, HttpServletResponse response) {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user.getEmail(), null, List.of());
        securityContext.setAuthentication(authentication);
        securityContextRepository.saveContext(securityContext, request, response);
    }

    @Override
    public FormResult changePassword(String userId, ChangePasswordForm form) {

        FormResult formResult = new FormResult();

        User user = entitlementService.getUserOrFail(userId);

        if (user.getPasswordHash() != null) {
            validateMandatory(formResult, "currentPassword", form.getCurrentPassword());
            if (!Strings.isNullOrEmpty(form.getCurrentPassword()) && !passwordEncoder.matches(form.getCurrentPassword(), user.getPasswordHash())) {
                CollectionsTools.getOrCreateEmptyArrayList(formResult.getValidationErrorsByField(), "currentPassword", String.class).add("error.currentPasswordMismatch");
            }
        }

        validateMandatory(formResult, "newPassword", form.getNewPassword());
        validateMandatory(formResult, "newPasswordConfirmation", form.getNewPasswordConfirmation());
        if (!Objects.equals(form.getNewPassword(), form.getNewPasswordConfirmation())) {
            CollectionsTools.getOrCreateEmptyArrayList(formResult.getValidationErrorsByField(), "newPasswordConfirmation", String.class).add("error.passwordNotEqual");
        }

        if (!formResult.isSuccess()) {
            return formResult;
        }

        user.setPasswordHash(passwordEncoder.encode(form.getNewPassword()));
        user.setPasswordLastChange(new Date());
        userRepository.save(user);

        return formResult;
    }

    private String generateCode() {
        return String.format("%06d", ThreadLocalRandom.current().nextInt(1_000_000));
    }

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
            page = userRepository.findAll(PageRequest.of(pageId - 1, paginationService.getItemsPerPage(), Direction.ASC, "email"));
        } else {
            page = userRepository.findAllSearch(search, PageRequest.of(pageId - 1, paginationService.getItemsPerPage(), Direction.ASC, "email"));
        }
        paginationService.wrap(result, page, com.foilen.crm.web.model.User.class);
        return result;
    }

    @Override
    public FormResult login(LoginForm form, HttpServletRequest request, HttpServletResponse response) {

        FormResult formResult = new FormResult();

        String email = normalizeEmail(form.getEmail());
        validateMandatory(formResult, "email", email);
        validateEmail(formResult, "email", email);
        validateMandatory(formResult, "password", form.getPassword());

        if (!formResult.isSuccess()) {
            return formResult;
        }

        User user = userRepository.findByEmail(email);
        if (user == null || user.getPasswordHash() == null || !passwordEncoder.matches(form.getPassword(), user.getPasswordHash())) {
            formResult.getGlobalErrors().add("error.login");
            return formResult;
        }
        if (user.isDisabled()) {
            formResult.getGlobalErrors().add("error.userDisabled");
            return formResult;
        }

        logger.info("User {} logged in with a password", user.getEmail());
        authenticate(user, request, response);

        user.setLastLogin(new Date());
        userRepository.save(user);

        return formResult;
    }

    @Override
    public FormResult loginWithCode(LoginWithCodeForm form, HttpServletRequest request, HttpServletResponse response) {

        FormResult formResult = new FormResult();

        String email = normalizeEmail(form.getEmail());
        String code = form.getCode() == null ? null : form.getCode().trim();

        validateMandatory(formResult, "email", email);
        validateEmail(formResult, "email", email);
        validateMandatory(formResult, "code", code);

        if (!formResult.isSuccess()) {
            return formResult;
        }

        User user = userRepository.findByEmail(email);
        if (user == null || !StringTools.safeEquals(user.getLoginCode(), code)) {
            formResult.getGlobalErrors().add("error.login");
            return formResult;
        }
        if (user.isDisabled()) {
            formResult.getGlobalErrors().add("error.userDisabled");
            return formResult;
        }
        if (user.getLoginCodeExpiration() == null || user.getLoginCodeExpiration().before(new Date())) {
            formResult.getGlobalErrors().add("error.expired");
            return formResult;
        }

        logger.info("User {} logged in with a code", user.getEmail());
        authenticate(user, request, response);

        // Consume the code (single use)
        user.setLastLogin(new Date());
        user.setLoginCode(null);
        user.setLoginCodeExpiration(null);
        userRepository.save(user);

        return formResult;
    }

    @Override
    public FormResult loginWithCodeRequest(LoginWithCodeRequestForm form) {

        FormResult formResult = new FormResult();

        String email = normalizeEmail(form.getEmail());
        validateMandatory(formResult, "email", email);
        validateEmail(formResult, "email", email);

        if (!formResult.isSuccess()) {
            return formResult;
        }

        // Get or create the user (the first user ever created is admin)
        User user = userRepository.findByEmail(email);
        if (user == null) {
            logger.info("Creating user {}", email);
            user = new User(email, userRepository.count() == 0);
            user.setCreationDate(new Date());
        }

        if (user.isDisabled()) {
            formResult.getGlobalErrors().add("error.userDisabled");
            return formResult;
        }

        // Rate limit: 1 code request per minute
        if (user.getLoginCodeLastGenerated() != null && System.currentTimeMillis() - user.getLoginCodeLastGenerated().getTime() < CODE_RATE_LIMIT_MILLIS) {
            formResult.getGlobalErrors().add("error.codeTooFrequent");
            return formResult;
        }

        String code = generateCode();
        user.setLoginCode(code);
        user.setLoginCodeExpiration(new Date(System.currentTimeMillis() + CODE_VALIDITY_MILLIS));
        user.setLoginCodeLastGenerated(new Date());
        userRepository.save(user);

        logger.info("User {} generated a login code", email);
        sendLoginCodeEmail(email, code);

        return formResult;
    }

    @Override
    public FormResult logout(HttpServletRequest request, HttpServletResponse response) {

        FormResult formResult = new FormResult();

        SecurityContextHolder.clearContext();
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        securityContextRepository.saveContext(SecurityContextHolder.createEmptyContext(), request, response);

        return formResult;
    }

    private String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }

    private void sendLoginCodeEmail(String email, String code) {

        String to = email;
        String subject = messageSource.getMessage("email.loginCode.subject", new Object[]{}, LocaleContextHolder.getLocale());
        String body = messageSource.getMessage("email.loginCode.body", new Object[]{code}, LocaleContextHolder.getLocale());

        if (mailForceEmailTo != null) {
            subject = "[FORCED] " + to + " | " + subject;
            to = mailForceEmailTo;
            logger.warn("Forcing email to {}", to);
        }

        EmailBuilder emailBuilder = new EmailBuilder();
        emailBuilder.setFrom(mailFrom);
        emailBuilder.addTo(to);
        emailBuilder.setSubject(subject);
        emailBuilder.setBodyTextFromString(body);

        emailService.sendEmail(emailBuilder);
    }

    @Override
    public FormResult updateAdmin(String userId, String id, UpdateUserAdminForm form) {

        FormResult formResult = new FormResult();

        // Validation
        entitlementService.canManageUsersOrFail(userId);
        User user = validateUserById(formResult, "id", id);

        if (!formResult.isSuccess()) {
            return formResult;
        }

        if (!form.isAdmin() && StringTools.safeEquals(userId, user.getEmail())) {
            formResult.getGlobalErrors().add("error.cannotRemoveOwnAdmin");
            return formResult;
        }

        // Update
        user.setAdmin(form.isAdmin());
        userRepository.save(user);

        return formResult;
    }

    @Override
    public FormResult updateDisabled(String userId, String id, UpdateUserDisabledForm form) {

        FormResult formResult = new FormResult();

        // Validation
        entitlementService.canManageUsersOrFail(userId);
        User user = validateUserById(formResult, "id", id);

        if (!formResult.isSuccess()) {
            return formResult;
        }

        if (form.isDisabled() && StringTools.safeEquals(userId, user.getEmail())) {
            formResult.getGlobalErrors().add("error.cannotDisableOwnAccount");
            return formResult;
        }

        // Update
        user.setDisabled(form.isDisabled());
        userRepository.save(user);

        return formResult;
    }

}
