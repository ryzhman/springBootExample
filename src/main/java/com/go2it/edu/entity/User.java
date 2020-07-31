package com.go2it.edu.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.go2it.edu.config.RolesConverter;

import javax.persistence.*;
import java.util.List;

/**
 * Entity that is stored in DB
 *
 */
@Entity
public class User {
    @Id
    @GeneratedValue()
    private long  id;
    @Column(nullable = false, unique = true)
    private String userName;
    private String password;
    private boolean isActive;
    @Convert(converter = RolesConverter.class)
    private List<String> roles;

    public User() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * By default JacksonJSON searches for getIsActive() getter to deserialize JSON
     *
     * @return
     */
    @JsonProperty(value = "isActive")
    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}
