package com.thienan.lovebox.entity;

import com.thienan.lovebox.entity.audit.DateAudit;

import javax.persistence.*;

@Entity
@Table(name = "photos")
public class PhotoEntity extends DateAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String url;

    public PhotoEntity() {
    }

    public PhotoEntity(String url) {
        this.url = url;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
