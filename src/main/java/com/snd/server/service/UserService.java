package com.snd.server.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import com.snd.server.constant.MonoConstant;
import com.snd.server.dto.request.AdminUserRequest;
import com.snd.server.dto.request.CustomerUserRequest;
import com.snd.server.dto.request.ResetPasswordRequest;
import com.snd.server.dto.request.RoleChangeRequest;
import com.snd.server.dto.request.StatusChangeRequest;
import com.snd.server.dto.response.CustomerUserResponse;
import com.snd.server.enums.ProviderEnum;
import com.snd.server.enums.UserStatusEnum;
import com.snd.server.event.EventType;
import com.snd.server.event.domain.OtpEvent;
import com.snd.server.event.publisher.OtpPublisher;
import com.snd.server.exception.AppException;
import com.snd.server.mapper.UserMapper;
import com.snd.server.model.Role;
import com.snd.server.model.User;
import com.snd.server.repository.RoleRepository;
import com.snd.server.repository.UserRepository;
import com.snd.server.utils.PasswordValidator;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final OtpPublisher otpPublisher;
    private final RoleRepository roleRepository;

    public UserService(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder,
            OtpPublisher otpPublisher, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.otpPublisher = otpPublisher;
        this.roleRepository = roleRepository;
    }

    public User findUserById(String uuid) {
        return userRepository.findById(uuid).orElseThrow(() -> new AppException("User not found"));
    }

    public CustomerUserResponse getUserByToken() {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String uuid = jwt.getClaimAsString(MonoConstant.UUID);
        User user = findUserById(uuid);
        return userMapper.customerToDto(user);
    }

    public CustomerUserResponse updateAccount(CustomerUserRequest request) {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String token = jwt.getSubject();
        User user = userRepository.findByEmail(token)
                .orElseThrow(() -> new AppException(MonoConstant.EMAIL_NOT_FOUND));
        userMapper.customerToUpdated(user, request);
        userRepository.save(user);
        return userMapper.customerToDto(user);
    }

    public void resetPassword(ResetPasswordRequest request) {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = jwt.getSubject();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(MonoConstant.EMAIL_NOT_FOUND));
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new AppException(MonoConstant.PASSWORD_OLD_INCORRECT);
        }

        if (!PasswordValidator.isStrongPassword(request.getNewPassword())) {
            throw new AppException(MonoConstant.PASSWORD_REQUIREMENTS_MESSAGE);

        }

        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new AppException(MonoConstant.PASSWORD_NEW_SAME_AS_OLD);
        }

        if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
            throw new AppException(MonoConstant.PASSWORD_NEW_MISMATCH);
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        OtpEvent otpEvent = OtpEvent.builder()
                .eventType(EventType.RESET_PASSWORD)
                .fullName(user.getFullName())
                .email(email)
                .otp(null)
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

    // Admin
    public void createUser(AdminUserRequest request) {
        validateEmailRegister(request.getEmail());
        Role role = roleRepository.findByName(request.getRoleEnum())
                .orElseThrow(() -> new AppException("Default role not found"));
        User user = userMapper.adminUserToEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setUserStatus(UserStatusEnum.ACTIVE);
        user.setRole(role);
        user.setProvider(ProviderEnum.LOCAL);
        userRepository.save(user);
    }

    public void changeRole(RoleChangeRequest request) {
        User user = userRepository.findById(request.getId())
                .orElseThrow(() -> new AppException(MonoConstant.USER_NOT_FOUND));
        Role role = roleRepository.findByName(request.getRoleEnum())
                .orElseThrow(() -> new AppException("Default role not found"));
        user.setRole(role);
        userRepository.save(user);
    }

    public void changeStatus(StatusChangeRequest request) {
        User user = userRepository.findById(request.getId())
                .orElseThrow(() -> new AppException(MonoConstant.USER_NOT_FOUND));
        user.setUserStatus(request.getUserStatusEnum());
        userRepository.save(user);
    }

    public CustomerUserResponse getUserById(String id) {
        User user = userRepository.findById(id).orElseThrow(() -> new AppException(MonoConstant.USER_NOT_FOUND));
        return userMapper.customerToDto(user);
    }

    public void deleteUsers(List<String> userIds) {
        List<String> existingUserIds = userRepository.findAllById(userIds)
                .stream()
                .map(User::getId)
                .collect(Collectors.toList());

        if (existingUserIds.isEmpty()) {
            throw new AppException(MonoConstant.DELETE_NOT_USERS);
        }
        userRepository.deleteAllById(existingUserIds);
    }

    // public SimplifiedPageResponse<AdminUserResponse> getAllUsers(Pageable
    // pageable) {
    // Page<User> userPage = userRepository.findAll(pageable);
    // List<AdminUserResponse> userDtoList = userPage.getContent().stream()
    // .map(users->userMapper.)
    // .collect(Collectors.toList());

    // Page<UserAdminDto> userDtoPage = new PageImpl<>(
    // userDtoList,
    // pageable,
    // userPage.getTotalElements());
    // return new SimplifiedPageResponse<>(userDtoPage);
    // }

}
