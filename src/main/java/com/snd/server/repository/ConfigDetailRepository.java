package com.snd.server.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.snd.server.model.Config;
import com.snd.server.model.ConfigDetail;

@Repository
public interface ConfigDetailRepository extends JpaRepository<ConfigDetail, Long> {
    void deleteAllByConfig(Config config);

    List<ConfigDetail> findByConfig(Config config);
}
