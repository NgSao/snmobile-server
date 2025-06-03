package com.snd.server.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import com.snd.server.constant.MonoConstant;
import com.snd.server.dto.request.AddressRequest;
import com.snd.server.dto.response.AddressResponse;
import com.snd.server.exception.AppException;
import com.snd.server.mapper.UserMapper;
import com.snd.server.model.Address;
import com.snd.server.model.User;
import com.snd.server.repository.AddressRepository;
import com.snd.server.repository.UserRepository;

@Service
public class AddressService {
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;

    public AddressService(UserMapper userMapper, UserRepository userRepository, AddressRepository addressRepository) {
        this.userMapper = userMapper;
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;

    }

    public AddressResponse createAddress(AddressRequest request) {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String uuid = jwt.getClaimAsString(MonoConstant.UUID);
        User user = userRepository.findById(uuid)
                .orElseThrow(() -> new AppException(MonoConstant.USER_NOT_FOUND));
        Address address = userMapper.addressToEntity(request);
        address.setUser(user);
        if (Boolean.TRUE.equals(request.getActive())) {
            user.getAddresses().forEach(a -> a.setActive(false));
            address.setActive(true);
        }
        addressRepository.save(address);
        return userMapper.addressToDto(address);
    }

    public List<AddressResponse> getAllAddress() {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String uuid = jwt.getClaimAsString(MonoConstant.UUID);
        List<Address> addressPage = addressRepository.findAllByUserId(uuid);
        return addressPage.stream()
                .map(userMapper::addressToDto)
                .collect(Collectors.toList());
    }

    public void changeAddressStatus(String addressId) {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String uuid = jwt.getClaimAsString(MonoConstant.UUID);
        User user = userRepository.findById(uuid)
                .orElseThrow(() -> new AppException(MonoConstant.USER_NOT_FOUND));
        Address selectedAddress = addressRepository.findById(addressId)
                .orElseThrow(() -> new AppException(MonoConstant.ADDRESS_NOT_FOUND));

        if (!selectedAddress.getUser().getId().equals(user.getId())) {
            throw new AppException(MonoConstant.ADDRESS_NOT_BELONG_TO_USER);
        }
        user.getAddresses().forEach(address -> address.setActive(false));
        selectedAddress.setActive(true);
        addressRepository.saveAll(user.getAddresses());
    }

    public AddressResponse updateAddress(String addressId, AddressRequest request) {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String uuid = jwt.getClaimAsString(MonoConstant.UUID);
        User user = userRepository.findById(uuid)
                .orElseThrow(() -> new AppException(MonoConstant.USER_NOT_FOUND));
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new AppException(MonoConstant.ADDRESS_NOT_FOUND));
        if (address.getUser() == null || !address.getUser().getId().equals(user.getId())) {
            throw new AppException(MonoConstant.ADDRESS_NOT_BELONG_TO_USER_OR_USER_NOT_LINKED);
        }

        userMapper.addressToUpdated(address, request);

        if (Boolean.TRUE.equals(request.getActive())) {
            user.getAddresses().forEach(a -> a.setActive(false));
            address.setActive(true);
        }
        Address saved = addressRepository.save(address);
        return userMapper.addressToDto(saved);
    }

    public void deleteAddress(String addressId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new AppException(MonoConstant.ADDRESS_NOT_FOUND));
        addressRepository.delete(address);
    }

}
