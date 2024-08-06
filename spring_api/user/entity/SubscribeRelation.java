package com.lab2ai.cosign.user.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
public class SubscribeRelation {
    @Id
    @GeneratedValue(generator = "uuid4")
    @GenericGenerator(name = "UUID", strategy = "uuid4") // 없으니 save시 에러난다
    @Type(type = "org.hibernate.type.UUIDCharType")
    @Column(columnDefinition = "CHAR(36) default uuid()")
    private UUID Id;

    @Column(columnDefinition = "CHAR(36)")
    private String mentorPageId;
    @Column(name = "user_id", columnDefinition = "CHAR(36)")
    private String userId;

    public SubscribeRelation(String mentorPageId, String userId) {
        this.mentorPageId = mentorPageId;
        this.userId = userId;
    }
}
