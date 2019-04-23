package com.thienan.lovebox.entity;

import com.thienan.lovebox.entity.audit.DateAudit;

import javax.persistence.*;

@Entity
@Table(name = "bff_requests")
public class BffRequestEntity extends DateAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "from_user_id")
    private UserEntity fromUser;

    @ManyToOne
    @JoinColumn(name = "to_user_id")
    private UserEntity toUser;

    @Column(nullable = false, length = 200)
    private String text;

    public BffRequestEntity() {
    }

    public BffRequestEntity(UserEntity fromUser, UserEntity toUser, String text) {
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.text = text;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserEntity getFromUser() {
        return fromUser;
    }

    public void setFromUser(UserEntity fromUser) {
        this.fromUser = fromUser;
    }

    public UserEntity getToUser() {
        return toUser;
    }

    public void setToUser(UserEntity toUser) {
        this.toUser = toUser;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
