package com.thienan.lovebox.entity;

import com.thienan.lovebox.entity.audit.DateAudit;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
public class UserEntity extends DateAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, length = 120)
    private String username;

    @Column(unique = true, length = 120)
    private String email;

    @Column(nullable = false, length = 50)
    private String firstName;

    @Column(nullable = false, length = 50)
    private String lastName;

    @Column(nullable = false)
    private String password;

    @Column(length = 50)
    private String moodMessage;

    @OneToOne
    private PhotoEntity photo;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<RoleEntity> roles = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_follows",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "following_id"))
    private Set<UserEntity> following;

    @ManyToMany(mappedBy = "following")
    private Set<UserEntity> followers;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "bff_detail_id")
    private BffDetailEntity bffDetail;

    public UserEntity() {
    }

    public UserEntity(String username, String email, String firstName, String lastName,
                      String password, String moodMessage, BffDetailEntity bffDetail) {
        this.username = username;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.moodMessage = moodMessage;
        this.bffDetail = bffDetail;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMoodMessage() {
        return moodMessage;
    }

    public void setMoodMessage(String moodMessage) {
        this.moodMessage = moodMessage;
    }

    public PhotoEntity getPhoto() {
        return photo;
    }

    public void setPhoto(PhotoEntity photo) {
        this.photo = photo;
    }

    public Set<RoleEntity> getRoles() {
        return roles;
    }

    public void setRoles(Set<RoleEntity> roles) {
        this.roles = roles;
    }

    public Set<UserEntity> getFollowing() {
        return following;
    }

    public void setFollowing(Set<UserEntity> following) {
        this.following = following;
    }

    public Set<UserEntity> getFollowers() {
        return followers;
    }

    public void setFollowers(Set<UserEntity> followers) {
        this.followers = followers;
    }

    private void addFollower(UserEntity user) {
        followers.add(user);
    }

    private void removeFollower(UserEntity user) {
        followers.remove(user);
    }

    public void addFollowing(UserEntity user) {
        following.add(user);
        user.addFollower(this);
    }

    public void removeFollowing(UserEntity user) {
        following.remove(user);
        user.removeFollower(this);
    }

    public BffDetailEntity getBffDetail() {
        return bffDetail;
    }

    public void setBffDetail(BffDetailEntity bffDetail) {
        this.bffDetail = bffDetail;
    }
}
