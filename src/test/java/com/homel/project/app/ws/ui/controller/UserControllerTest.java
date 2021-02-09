package com.homel.project.app.ws.ui.controller;

import com.homel.project.app.ws.io.entity.UserEntity;
import com.homel.project.app.ws.service.UserService;
import com.homel.project.app.ws.shared.dto.AddressDto;
import com.homel.project.app.ws.shared.dto.UserDto;
import com.homel.project.app.ws.ui.model.response.UserRest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class UserControllerTest {

    @InjectMocks
    UserController userController;

    @Mock
    UserService userService;

    UserDto userDto;
    String userId = "testUserId";
    String encryptedPassword = "userPassword";
    String email = "test@test.ru";
    String emailVerificationToken = "emailToken";
    String addressId = "AddressId";
    String userFirstName = "Vlad";
    String userLastName = "God";
    String addressType = "shipping";
    String userPassword = "password";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        userDto = new UserDto();
        userDto.setAddresses(getAddressesDto());
        userDto.setEmailVerificationToken(null);
        userDto.setEmailVerificationStatus(Boolean.FALSE);
        userDto.setUserId(userId);
        userDto.setEncryptedPassword(encryptedPassword);
        userDto.setFirstName(userFirstName);
        userDto.setLastName(userLastName);
        userDto.setPassword(userPassword);
        userDto.setEmail(email);

    }

    @Test
    void getUser() {
        when(userService.getUserByUserId(anyString())).thenReturn(userDto);

        UserRest userRest = userController.getUser(userId);
        assertNotNull(userRest);
        assertEquals(userId, userRest.getUserId());
        assertEquals(userDto.getFirstName(), userRest.getFirstName());
        assertEquals(userDto.getLastName(), userRest.getLastName());
        assertEquals(userDto.getAddresses().size(), userRest.getAddresses().size());
    }

    private List<AddressDto> getAddressesDto() {
        AddressDto addressDto = new AddressDto();
        addressDto.setType(addressType);
        addressDto.setCity("Vancouver");
        addressDto.setCountry("Canada");
        addressDto.setPostalCode("AVVCCC");
        addressDto.setStreetName("123 Street name");

        AddressDto billingAddressDto = new AddressDto();
        billingAddressDto.setType("billing");
        billingAddressDto.setCity("Vancouver");
        billingAddressDto.setCountry("Canada");
        billingAddressDto.setPostalCode("AVVCCC");
        billingAddressDto.setStreetName("123 Street name");

        return Arrays.asList(addressDto, billingAddressDto);
    }
}