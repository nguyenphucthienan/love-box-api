package com.thienan.lovebox.entity;

import com.thienan.lovebox.entity.audit.DateAudit;

import javax.persistence.*;

@Entity
@Table(name = "bff_details")
public class BffDetailEntity extends DateAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "first_user_id")
    private UserEntity firstUser;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "second_user_id")
    private UserEntity secondUser;

    private String description;

    public BffDetailEntity(UserEntity firstUser, UserEntity secondUser, String description) {
        this.firstUser = firstUser;
        this.secondUser = secondUser;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserEntity getFirstUser() {
        return firstUser;
    }

    public void setFirstUser(UserEntity firstUser) {
        this.firstUser = firstUser;
    }

    public UserEntity getSecondUser() {
        return secondUser;
    }

    public void setSecondUser(UserEntity secondUser) {
        this.secondUser = secondUser;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
