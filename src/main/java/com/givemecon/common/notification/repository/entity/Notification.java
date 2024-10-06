package com.givemecon.common.notification.repository.entity;

import com.givemecon.domain.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE notification SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Entity
public class Notification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String content;

    private boolean isRead;

    public Notification(String username, String content) {
        this.username = username;
        this.content = content;
    }

    public void read() {
        this.isRead = true;
    }
}
