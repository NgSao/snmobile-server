package com.snd.server.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.snd.server.model.Session;
import com.snd.server.model.User;

public interface SessionRepository extends JpaRepository<Session, Long> {
    List<Session> findTop3ByUserOrderByLastActivityDesc(User user);

    Optional<Session> findByPayload(String payload);

}
