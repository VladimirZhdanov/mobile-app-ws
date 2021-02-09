package com.homel.project.app.ws.io.entity;

import javax.persistence.*;
import java.io.Serializable;

@Entity(name = "password_reset_tokens")
public class PasswordResetTokenEntity implements Serializable {
    private static final long serialVersionUID = -3341046212719128812L;

    @Id
    @GeneratedValue
    private Long id;

    private String token;

    @OneToOne
    @JoinColumn(name = "user_id")
    private UserEntity userDetails;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserEntity getUserDetails() {
        return userDetails;
    }

    public void setUserDetails(UserEntity userDetails) {
        this.userDetails = userDetails;
    }
}
