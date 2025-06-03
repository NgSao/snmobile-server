package com.snd.server.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.snd.server.model.Address;

@Repository
public interface AddressRepository extends JpaRepository<Address, String> {
    List<Address> findAllByUserId(String userId);

}
