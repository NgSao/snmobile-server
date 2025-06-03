package com.snd.server.mapper;

import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.snd.server.dto.request.AddressRequest;
import com.snd.server.dto.request.AdminUserRequest;
import com.snd.server.dto.request.CustomerUserRequest;
import com.snd.server.dto.response.AddressResponse;
import com.snd.server.dto.response.CustomerUserResponse;
import com.snd.server.dto.response.SessionResponse;
import com.snd.server.model.Address;
import com.snd.server.model.Session;
import com.snd.server.model.User;

@Component
public class UserMapper {

        public CustomerUserResponse customerToDto(User user) {
                return CustomerUserResponse.builder()
                                .id(user.getId())
                                .fullName(user.getFullName())
                                .email(user.getEmail())
                                .phone(user.getPhone())
                                .profileImageUrl(user.getProfileImageUrl())
                                .birthday(user.getBirthday())
                                .role(user.getRole().getName().getRoleName())
                                .gender(user.getGender())
                                .lastLoginDate(user.getLastLoginDate())
                                .createdAt(user.getCreatedAt())
                                .addresses(user.getAddresses().stream()
                                                .map(addresss -> this.addressToDto(addresss))
                                                .collect(Collectors.toSet()))
                                .build();
        }

        public AddressResponse addressToDto(Address address) {
                return AddressResponse.builder()
                                .id(address.getId())
                                .addressType(address.getAddressType().getTypeName())
                                .fullName(address.getFullName())
                                .phone(address.getPhone())
                                .city(address.getCity())
                                .district(address.getDistrict())
                                .street(address.getStreet())
                                .addressDetail(address.getAddressDetail())
                                .active(address.getActive())
                                .build();
        }

        public void customerToUpdated(User user, CustomerUserRequest request) {
                if (request.getFullName() != null) {
                        user.setFullName(request.getFullName());
                }
                if (request.getProfileImageUrl() != null) {
                        user.setProfileImageUrl(request.getProfileImageUrl());
                }
                if (request.getPhone() != null) {
                        user.setPhone(request.getPhone());
                }
                if (request.getBirthday() != null) {
                        user.setBirthday(request.getBirthday());
                }
                if (request.getGender() != null) {
                        user.setGender(request.getGender());
                }
        }

        public Address addressToEntity(AddressRequest request) {
                return Address.builder()
                                .addressType(request.getAddressType())
                                .fullName(request.getFullName())
                                .phone(request.getPhone())
                                .city(request.getCity())
                                .district(request.getDistrict())
                                .street(request.getStreet())
                                .addressDetail(request.getAddressDetail())
                                .active(request.getActive() != null ? request.getActive() : false)
                                .build();
        }

        public Address addressToUpdated(Address address, AddressRequest request) {
                if (request.getAddressType() != null) {
                        address.setAddressType(request.getAddressType());
                }
                if (request.getFullName() != null) {
                        address.setFullName(request.getFullName());
                }
                if (request.getPhone() != null) {
                        address.setPhone(request.getPhone());
                }
                if (request.getCity() != null) {
                        address.setCity(request.getCity());
                }
                if (request.getDistrict() != null) {
                        address.setDistrict(request.getDistrict());
                }
                if (request.getStreet() != null) {
                        address.setStreet(request.getStreet());
                }
                if (request.getAddressDetail() != null) {
                        address.setAddressDetail(request.getAddressDetail());
                }
                if (request.getActive() != null) {
                        address.setActive(request.getActive());
                }
                return address;
        }

        public SessionResponse sessionToDto(Session session) {
                return SessionResponse.builder()
                                .id(session.getId())
                                .ipAddress(session.getIpAddress())
                                .userAgent(session.getUserAgent())
                                .payload(session.getPayload())
                                .address(session.getAddress())
                                .latitude(session.getLatitude())
                                .longitude(session.getLongitude())
                                .lastActivity(session.getLastActivity())
                                .build();
        }

        public User adminUserToEntity(AdminUserRequest request) {
                return User.builder()
                                .fullName(request.getFullName())
                                .email(request.getEmail())
                                .phone(request.getPhone())
                                .password(request.getPassword())
                                .addresses(request.getAddress() != null
                                                ? request.getAddress().stream()
                                                                .map(this::addressToEntity)
                                                                .collect(Collectors.toSet())
                                                : null)
                                .build();
        }

}
