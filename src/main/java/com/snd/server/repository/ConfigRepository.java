package com.snd.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.snd.server.model.Config;

@Repository
public interface ConfigRepository extends JpaRepository<Config, Long> {

}
