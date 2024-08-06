package com.lab2ai.cosign.user.service;

import com.lab2ai.cosign.user.entity.SubscribeRelation;
import com.lab2ai.cosign.user.repository.SubscribeRelationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class SubscribeService {
    @Autowired
    private SubscribeRelationRepository subscribeRelationRepository;

    public boolean subscribe(String mentorPageId, UUID userId) {
        SubscribeRelation relation = subscribeRelationRepository.findByMentorPageIdAndUserId(mentorPageId, userId.toString()).orElseGet(() -> null);

        if(relation == null) {
            subscribeRelationRepository.save(new SubscribeRelation(mentorPageId, userId.toString()));
            return true;
        } else {
            return false;
        }
    }

    public boolean unsubscribe(String mentorPageId, UUID userId) {
        SubscribeRelation relation = subscribeRelationRepository.findByMentorPageIdAndUserId(mentorPageId, userId.toString()).orElseGet(() -> null);

        if(relation != null) {
            subscribeRelationRepository.delete(relation);
            return true;
        } else {
            return false;
        }
    }

    public SubscribeRelation getSubscribeRelation(String mentorPageId, UUID userId) {
        SubscribeRelation relation = subscribeRelationRepository.findByMentorPageIdAndUserId(mentorPageId, userId.toString()).orElseGet(() -> null);

        return relation;
    }


}
