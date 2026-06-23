package org.example.ecommerceapi.domain.user.service;

import org.example.ecommerceapi.common.exception.BusinessException;
import org.example.ecommerceapi.common.exception.ErrorCode;
import org.example.ecommerceapi.domain.user.dto.LoginResponse;
import org.example.ecommerceapi.domain.user.entity.AppUser;
import org.example.ecommerceapi.domain.user.repository.AppUserRepository;
import org.example.ecommerceapi.security.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    private static final String EMAIL = "user@example.com";
    private static final String RAW_PASSWORD = "password";
    private static final String ENCODED_PASSWORD = "encodedPassword";
    private static final String ACCESS_TOKEN = "accessToken";
    private static final Long USER_ID = 1L;

    @Mock
    private AppUserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private AuthService authService;

    @Test
    void register_withNewEmail_savesEncodedPasswordUser() {
        when(userRepository.existsByEmail(EMAIL))
                .thenReturn(false);
        when(passwordEncoder.encode(RAW_PASSWORD))
                .thenReturn(ENCODED_PASSWORD);

        authService.register(EMAIL, RAW_PASSWORD);

        ArgumentCaptor<AppUser> captor = ArgumentCaptor.forClass(AppUser.class);
        verify(userRepository).save(captor.capture());

        AppUser savedUser = captor.getValue();
        assertEquals(EMAIL, savedUser.getEmail());
        assertEquals(ENCODED_PASSWORD, savedUser.getPassword());
    }

    @Test
    void register_withExistingEmail_throwsEmailAlreadyExists() {
        when(userRepository.existsByEmail(EMAIL))
                .thenReturn(true);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> authService.register(EMAIL, RAW_PASSWORD)
        );

        assertEquals(ErrorCode.EMAIL_ALREADY_EXISTS, exception.getErrorCode());
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(AppUser.class));
    }

    @Test
    void login_withValidCredentials_returnsAccessToken() {
        AppUser user = createUserWithId();

        when(userRepository.findByEmail(EMAIL))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.matches(RAW_PASSWORD, ENCODED_PASSWORD))
                .thenReturn(true);
        when(jwtTokenProvider.generateToken(USER_ID))
                .thenReturn(ACCESS_TOKEN);

        LoginResponse response = authService.login(EMAIL, RAW_PASSWORD);

        assertEquals(ACCESS_TOKEN, response.accessToken());
    }

    @Test
    void login_withUnknownEmail_throwsInvalidLogin() {
        when(userRepository.findByEmail(EMAIL))
                .thenReturn(Optional.empty());

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> authService.login(EMAIL, RAW_PASSWORD)
        );

        assertEquals(ErrorCode.INVALID_LOGIN, exception.getErrorCode());
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtTokenProvider, never()).generateToken(anyLong());
    }

    @Test
    void login_withWrongPassword_throwsInvalidLogin() {
        when(userRepository.findByEmail(EMAIL))
                .thenReturn(Optional.of(createUserWithId()));
        when(passwordEncoder.matches(RAW_PASSWORD, ENCODED_PASSWORD))
                .thenReturn(false);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> authService.login(EMAIL, RAW_PASSWORD)
        );

        assertEquals(ErrorCode.INVALID_LOGIN, exception.getErrorCode());
        verify(jwtTokenProvider, never()).generateToken(anyLong());
    }

    private AppUser createUserWithId() {
        AppUser user = AppUser.createUser(EMAIL, ENCODED_PASSWORD);
        ReflectionTestUtils.setField(user, "id", USER_ID);
        return user;
    }
}
