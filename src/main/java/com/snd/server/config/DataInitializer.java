package com.snd.server.config;

import com.snd.server.enums.GuardNameEnum;
import com.snd.server.enums.ProviderEnum;
import com.snd.server.enums.RoleEnum;
import com.snd.server.enums.UserStatusEnum;
import com.snd.server.exception.AppException;
import com.snd.server.model.Role;
import com.snd.server.model.User;
import com.snd.server.repository.RoleRepository;
import com.snd.server.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.gmail}")
    private String adminEmail;

    @Value("${admin.name}")
    private String adminName;

    @Value("${admin.password}")
    private String adminPassword;

    public DataInitializer(RoleRepository roleRepository, UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        try {
            logger.info("Starting data initialization...");

            long countRoles = roleRepository.count();
            if (countRoles == 0) {
                for (RoleEnum roleEnum : RoleEnum.values()) {
                    createRoleIfNotFound(roleEnum);
                }
                logger.info("Initialized {} roles", RoleEnum.values().length);
            } else {
                logger.info("Skipping role initialization, {} roles already exist", countRoles);
            }

            long countUsers = userRepository.count();
            if (countUsers == 0) {
                Role adminRole = roleRepository.findByName(RoleEnum.ROLE_ADMIN)
                        .orElseThrow(() -> new AppException("Role ROLE_ADMIN not found"));

                User admin = User.builder()
                        .fullName(adminName)
                        .email(adminEmail)
                        .password(passwordEncoder.encode(adminPassword))
                        .userStatus(UserStatusEnum.ACTIVE)
                        .provider(ProviderEnum.LOCAL)
                        .role(adminRole)
                        .build();

                userRepository.save(admin);
                logger.info("Created admin user with email: {}", admin.getEmail());
            } else {
                logger.info("Skipping user initialization, {} users already exist", countUsers);
            }

            String managerEmail = "manager@gmail.com";
            if (!userRepository.existsByEmail(managerEmail)) {
                Role managerRole = roleRepository.findByName(RoleEnum.ROLE_QUAN_LY_CUA_HANG)
                        .orElseThrow(() -> new AppException("ROLE_QUAN_LY_CUA_HANG not found"));

                User manager = User.builder()
                        .fullName("Manager")
                        .email(managerEmail)
                        .password(passwordEncoder.encode("manager123"))
                        .userStatus(UserStatusEnum.ACTIVE)
                        .provider(ProviderEnum.LOCAL)
                        .role(managerRole)
                        .build();
                userRepository.save(manager);
                logger.info("Created manager user: {}", manager.getEmail());
            }

            String staffEmail = "staff@gmail.com";
            if (!userRepository.existsByEmail(staffEmail)) {
                Role staffRole = roleRepository.findByName(RoleEnum.ROLE_NHAN_VIEN_BAN_HANG)
                        .orElseThrow(() -> new RuntimeException("ROLE_NHAN_VIEN_BAN_HANG not found"));

                User staff = User.builder()
                        .fullName("Sales Staff")
                        .email(staffEmail)
                        .password(passwordEncoder.encode("staff123"))
                        .userStatus(UserStatusEnum.ACTIVE)
                        .provider(ProviderEnum.LOCAL)
                        .role(staffRole)
                        .build();
                userRepository.save(staff);
                logger.info("Created staff user: {}", staff.getEmail());
            }

            logger.info("Data initialization completed.");
        } catch (Exception e) {
            logger.error("Data initialization failed: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to initialize data", e);
        }
    }

    private void createRoleIfNotFound(RoleEnum roleEnum) {
        if (!roleRepository.existsByName(roleEnum)) {
            Role role = Role.builder()
                    .name(roleEnum)
                    .guardName(GuardNameEnum.WEB)
                    .build();
            roleRepository.save(role);
            logger.info("Created role: {}", roleEnum.getRoleName());
        }
    }
}