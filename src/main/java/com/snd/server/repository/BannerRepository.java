package com.snd.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.snd.server.model.Banner;

@Repository
public interface BannerRepository extends JpaRepository<Banner, Long> {

}
