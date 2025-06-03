package com.snd.server.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.snd.server.constant.ApiPathConstant;
import com.snd.server.dto.request.AddressRequest;
import com.snd.server.dto.response.AddressResponse;
import com.snd.server.service.AddressService;

import jakarta.validation.Valid;

@RestController
@RequestMapping(ApiPathConstant.API_PREFIX)
public class AddressController {
    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @PostMapping("/address")
    public ResponseEntity<AddressResponse> createAddress(@Valid @RequestBody AddressRequest request) {
        return ResponseEntity.ok().body(addressService.createAddress(request));
    }

    @GetMapping("/address")
    public ResponseEntity<List<AddressResponse>> getAllAddress() {
        return ResponseEntity.ok().body(addressService.getAllAddress());
    }

    @PutMapping("/address/active/{addressId}")
    public ResponseEntity<Boolean> changeAddressStatus(@PathVariable String addressId) {
        addressService.changeAddressStatus(addressId);
        return ResponseEntity.ok().body(true);
    }

    @PutMapping("/address/{addressId}")
    public ResponseEntity<AddressResponse> updateAddress(@PathVariable String addressId,
            @RequestBody AddressRequest request) {
        return ResponseEntity.ok().body(addressService.updateAddress(addressId, request));
    }

    @DeleteMapping("/address/{addressId}")
    public ResponseEntity<Boolean> deleteAddress(@PathVariable String addressId) {
        addressService.deleteAddress(addressId);
        return ResponseEntity.ok().body(true);
    }
}
