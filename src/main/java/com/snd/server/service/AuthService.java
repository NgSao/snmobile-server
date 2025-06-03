package com.snd.server.service;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.snd.server.constant.MonoConstant;
import com.snd.server.dto.request.AuthRegisterRequest;
import com.snd.server.dto.request.EmailRequest;
import com.snd.server.dto.request.UserLoginRequest;
import com.snd.server.dto.request.VerifyRequest;
import com.snd.server.dto.response.AuthLoginResponse;
import com.snd.server.enums.ProviderEnum;
import com.snd.server.enums.RoleEnum;
import com.snd.server.enums.UserStatusEnum;
import com.snd.server.event.EventType;
import com.snd.server.event.domain.OtpEvent;
import com.snd.server.event.publisher.OtpPublisher;
import com.snd.server.exception.AppException;
import com.snd.server.model.Otp;
import com.snd.server.model.Role;
import com.snd.server.model.User;
import com.snd.server.repository.OtpRepository;
import com.snd.server.repository.RoleRepository;
import com.snd.server.repository.UserRepository;
import com.snd.server.utils.GenerateOTP;
import com.snd.server.utils.GeneratePassword;
import com.snd.server.utils.JwtUtil;
import com.snd.server.utils.PasswordValidator;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final OtpRepository otpRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtUtil jwtUtil;
    private final OtpPublisher otpPublisher;
    private final AuthenticationSuccessHandler loginSuccessHandler;

    public AuthService(UserRepository userRepository,
            RoleRepository roleRepository,
            OtpRepository otpRepository,
            PasswordEncoder passwordEncoder, AuthenticationManagerBuilder authenticationManagerBuilder,
            JwtUtil jwtUtil,
            OtpPublisher otpPublisher,
            AuthenticationSuccessHandler loginSuccessHandler) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.otpRepository = otpRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.jwtUtil = jwtUtil;
        this.otpPublisher = otpPublisher;
        this.loginSuccessHandler = loginSuccessHandler;

    }

    public void registerUser(AuthRegisterRequest request) {
        validateEmailRegister(request.getEmail());
        if (!PasswordValidator.isStrongPassword(request.getPassword())) {
            throw new AppException(MonoConstant.PASSWORD_REQUIREMENTS_MESSAGE);
        }
        Role role = roleRepository.findByName(RoleEnum.ROLE_KHACH_HANG)
                .orElseThrow(() -> new AppException("Default role not found"));
        String verificationCode = GenerateOTP.generate();
        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .password(passwordEncoder.encode(request.getPassword()))
                .userStatus(UserStatusEnum.UNVERIFIED)
                .role(role)
                .provider(ProviderEnum.LOCAL)
                .build();
        userRepository.save(user);
        Otp otp = Otp.builder()
                .email(request.getEmail())
                .otp(verificationCode)
                .expiresAt(Instant.now().plus(MonoConstant.EXPIRATION_OTP, ChronoUnit.SECONDS))
                .build();
        otpRepository.save(otp);

        OtpEvent otpEvent = OtpEvent.builder()
                .eventType(EventType.REGISTER_OTP)
                .fullName(request.getFullName())
                .email(request.getEmail())
                .otp(verificationCode)
                .build();
        otpPublisher.sendOtp(otpEvent);
    }

    public void verifyUser(VerifyRequest request) {
        Otp otp = otpRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(MonoConstant.OTP_NOT_FOUND));

        if (otp.getExpiresAt().isBefore(Instant.now())) {
            throw new AppException(MonoConstant.OTP_EXPIRED);
        }

        if (!otp.getOtp().equals(request.getOtp())) {
            int newAttempts = otp.getAttempts() + 1;
            otp.setAttempts(newAttempts);

            if (newAttempts >= 3) {
                otpRepository.delete(otp);
                throw new AppException(MonoConstant.OTP_RETRY_LIMIT_EXCEEDED);
            }
            otpRepository.save(otp);
            throw new AppException(String.format(MonoConstant.OTP_INVALID_REMAINING_ATTEMPTS, (3 - newAttempts)));
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(MonoConstant.USER_NOT_FOUND));
        user.setUserStatus(UserStatusEnum.ACTIVE);
        userRepository.save(user);
        otpRepository.delete(otp);
    }

    public void sendOtp(EmailRequest request) {
        User user = checkExistsEmail(request.getEmail());
        String optVery = GenerateOTP.generate();
        Otp otp = Otp.builder()
                .email(request.getEmail())
                .otp(optVery)
                .expiresAt(Instant.now().plus(MonoConstant.EXPIRATION_OTP, ChronoUnit.SECONDS))
                .build();
        otpRepository.save(otp);

        OtpEvent otpEvent = OtpEvent.builder()
                .eventType(EventType.VERIFY_OTP)
                .fullName(user.getFullName())
                .email(request.getEmail())
                .otp(optVery)
                .build();
        otpPublisher.sendOtp(otpEvent);
    }

    public void forgotPassword(VerifyRequest request) {
        Otp otp = otpRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(MonoConstant.OTP_NOT_FOUND));
        if (otp.getExpiresAt().isBefore(Instant.now())) {
            throw new AppException(MonoConstant.OTP_EXPIRED);
        }
        if (!otp.getOtp().equals(request.getOtp())) {
            int newAttempts = otp.getAttempts() + 1;
            otp.setAttempts(newAttempts);

            if (newAttempts >= 3) {
                otpRepository.delete(otp);
                throw new AppException(MonoConstant.OTP_RETRY_LIMIT_EXCEEDED);
            }
            otpRepository.save(otp);
            throw new AppException(String.format(MonoConstant.OTP_INVALID_REMAINING_ATTEMPTS, (3 - newAttempts)));
        }
        User existingUser = checkUserByEmailNotActive(request.getEmail());
        String newPassword = GeneratePassword.generate();
        existingUser.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(existingUser);
        otpRepository.delete(otp);

        OtpEvent otpEvent = OtpEvent.builder()
                .eventType(EventType.FORGOT_PASSWORD)
                .fullName(existingUser.getFullName())
                .email(request.getEmail())
                .otp(newPassword)
                .build();
        otpPublisher.sendOtp(otpEvent);

    }

    public void validateEmailRegister(String email) {
        Optional<User> existingUser = userRepository.findByEmail(email);
        if (existingUser.isPresent()) {
            User user = existingUser.get();
            if (user.getUserStatus() == UserStatusEnum.UNVERIFIED) {
                throw new AppException(MonoConstant.ACCOUNT_UNVERIFIED);
            }
            if (user.getUserStatus() == UserStatusEnum.BLOCKED) {
                throw new AppException(MonoConstant.ACCOUNT_LOCKED);
            }
            throw new AppException(MonoConstant.EMAIL_ALREADY_USED);
        }
    }

    // CheckEmail ch kích hoạt
    public User checkUserByEmailNotActive(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(MonoConstant.EMAIL_NOT_FOUND));
        if (user.getUserStatus() == UserStatusEnum.BLOCKED) {
            throw new AppException(MonoConstant.ACCOUNT_LOCKED);
        }

        return user;
    }

    // Check Email có trả về
    public User checkUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(MonoConstant.EMAIL_NOT_FOUND));

        if (user.getUserStatus() == UserStatusEnum.UNVERIFIED) {
            throw new AppException(MonoConstant.ACCOUNT_UNVERIFIED);
        }

        if (user.getUserStatus() == UserStatusEnum.BLOCKED) {
            throw new AppException(MonoConstant.ACCOUNT_LOCKED);
        }
        return user;
    }

    // Check Email trả về
    public User checkExistsEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(MonoConstant.EMAIL_NOT_FOUND));

        if (user.getUserStatus() == UserStatusEnum.BLOCKED) {
            throw new AppException(MonoConstant.ACCOUNT_LOCKED);
        }
        return user;
    }

    public AuthLoginResponse loginUser(UserLoginRequest request) {
        User user = checkUserByEmail(request.getEmail());
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                request.getEmail(), request.getPassword());
        Authentication authentication = authenticationManagerBuilder.getObject()
                .authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String access_token = jwtUtil.createAccessToken(request.getEmail());
        ((UsernamePasswordAuthenticationToken) authentication).setDetails(access_token);

        AuthLoginResponse userLoginResponse = new AuthLoginResponse();
        userLoginResponse.setAccessToken(access_token);
        userLoginResponse.setEmail(request.getEmail());
        user.setLastLoginDate(Instant.now());
        user.setRefreshToken(access_token);
        userRepository.save(user);

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest servletRequest = attributes.getRequest();
            HttpServletResponse servletResponse = attributes.getResponse();
            if (servletResponse != null) {
                try {
                    loginSuccessHandler.onAuthenticationSuccess(servletRequest, servletResponse, authentication);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ServletException e) {
                    e.printStackTrace();
                }
            }
        }

        return userLoginResponse;

    }

    public String refreshToken(String token) {
        String email = jwtUtil.decodedToken(token);
        String refreshToken = jwtUtil.createRefreshToken(email);
        return refreshToken;
    }

    @Scheduled(fixedRate = 180000)
    @Transactional
    public void deleteExpiredOtps() {
        Instant now = Instant.now();
        otpRepository.deleteAllByExpiresAtBefore(now);
    }

}
