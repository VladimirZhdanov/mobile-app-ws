package com.homel.project.app.ws.shared;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class UtilsTest {

    @Autowired
    Utils utils;

    @Test
    void whenGenerateUserIdShouldReturnCorrectUserId() {
        String userId = utils.generateAddressId(30);
        String userIdTwo = utils.generateAddressId(30);
        assertNotNull(userId);
        assertEquals(30, userId.length());
        assertNotSame(userId, userIdTwo);
    }

    @Test
    void hasTokenNotExpired() {
        //TODO - make test for generateEmailVerificationToke()
        String token = utils.generateEmailVerificationToken("123");
        boolean hasTokenExpired = Utils.hasTokenExpired(token);

        assertFalse(hasTokenExpired);
    }

    @Test
    void hasTokenExpired() {
        //TODO - make test for generateEmailVerificationToke()
        String token = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxMjMiLCJleHAiOjE2MTI5Mzk0MzN9.zkct7ZIEGUOzZdguyzbTnnfxSfw19trOSJ43Nf3pqMlM2YdB09-7YtKumEqk7s812WfAjhnvG6AUKaT7jgjLbw";
        boolean hasTokenExpired = Utils.hasTokenExpired(token);

        assertTrue(hasTokenExpired);
    }
}