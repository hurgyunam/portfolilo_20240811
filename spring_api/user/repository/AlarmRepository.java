package com.lab2ai.cosign.user.repository;

import com.lab2ai.cosign.mentor.data.MentorAlarm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.*;

@Repository
public class AlarmRepository {
    @Autowired
    private EntityManager entityManager;

    public List<MentorAlarm> findMentorAlarms(UUID userId) {
        String queryStr = "select a.mentorPageId, count(b.Id) as sign_count\n" +
                "\tfrom SubscribeRelation a\n" +
                "\tleft join Sign b\n" +
                "\ton a.mentorPageId = b.mentorPageId and b.endTime > now()\n" +
                "\twhere a.userId = :user_id\n" +
                "\tgroup by a.mentorPageId ";

        Query query = entityManager.createQuery(queryStr);
        query.setParameter("user_id", userId.toString());

        List<Object[]> objects = query.getResultList();
        Map<UUID, MentorAlarm> mapAlarms = new HashMap<>();

        for(Object[] alarm : objects) {
            UUID mentorPageId = UUID.fromString(alarm[0].toString());
            Integer signCount = Integer.parseInt(alarm[1].toString());

            mapAlarms.put(mentorPageId, new MentorAlarm(mentorPageId, signCount));
        }

        String mentorPageQueryStr = "select a.Id, a.title, a.profileImage\n" +
                "from MentorPage a\n" +
                "where a.Id in (:ids)";

        Query mentorPageQuery = entityManager.createQuery(mentorPageQueryStr);
        mentorPageQuery.setParameter("ids", Arrays.asList(mapAlarms.keySet().toArray()));

        objects = mentorPageQuery.getResultList();

        List<MentorAlarm> alarms = new ArrayList<>();

        for(Object[] object : objects) {
            UUID mentorPageId = (UUID) object[0];
            String title = object[1].toString();
            String profileImage = (String) object[2];

            MentorAlarm alarm = mapAlarms.get(mentorPageId);
            alarm.setTitle(title);
            alarm.setProfileImage(profileImage);
            alarms.add(alarm);
        }

        return alarms;
    }
}
