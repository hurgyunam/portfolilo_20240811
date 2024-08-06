package com.lab2ai.cosign.user.repository;

import com.lab2ai.cosign.user.entity.SubscribeRelation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SubscribeRelationRepository extends JpaRepository<SubscribeRelation, UUID> {
    Optional<SubscribeRelation> findByMentorPageIdAndUserId(String mentorPageId, String userId);
}
