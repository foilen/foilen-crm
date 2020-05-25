/*
    Foilen CRM
    https://github.com/foilen/foilen-crm
    Copyright (c) 2015-2020 Foilen (http://foilen.com)

    The MIT License
    http://opensource.org/licenses/MIT

 */
package com.foilen.crm.db.entities.user;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Version;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    @Version
    private long version;

    @Column(unique = true, nullable = false)
    private String userId;
    @Column(name = "isAdmin", nullable = false)
    private boolean admin;
    @Column(nullable = true)
    private String email;

    public User() {
    }

    public User(String userId, boolean admin) {
        this.userId = userId;
        this.admin = admin;
    }

    public String getEmail() {
        return email;
    }

    public Long getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

}
