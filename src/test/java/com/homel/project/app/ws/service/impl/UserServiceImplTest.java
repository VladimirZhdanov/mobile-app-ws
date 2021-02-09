package com.homel.project.app.ws.service.impl;

import com.homel.project.app.ws.exceptions.UserServiceException;
import com.homel.project.app.ws.io.entity.AddressEntity;
import com.homel.project.app.ws.io.entity.UserEntity;
import com.homel.project.app.ws.io.repositories.UserRepository;
import com.homel.project.app.ws.shared.AmazonSES;
import com.homel.project.app.ws.shared.Utils;
import com.homel.project.app.ws.shared.dto.AddressDto;
import com.homel.project.app.ws.shared.dto.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @InjectMocks
    UserServiceImpl userService;

    @Mock
    UserRepository userRepository;

    @Mock
    Utils utils;

    @Mock
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Mock
    AmazonSES amazonSES;

    String userId = "testUserId";
    String encryptedPassword = "userPassword";
    String email = "test@test.ru";
    UserEntity userEntity;
    String emailVerificationToken = "emailToken";
    String addressId = "AddressId";
    String userFirstName = "Vlad";
    String userLastName = "God";
    String addressType = "shipping";
    String userPassword = "password";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setFirstName(userFirstName);
        userEntity.setLastName(userLastName);
        userEntity.setUserId(userId);
        userEntity.setEmail(email);
        userEntity.setEmailVerificationToken(emailVerificationToken);
        userEntity.setEncryptedPassword(encryptedPassword);
        userEntity.setAddresses(getAddressesEntity());
    }

    @Test
    void whenGetUserVladShouldReturnUserWithVladFirstName() {

        when(userRepository.findByEmail(anyString())).thenReturn(userEntity);

        UserDto userDto = userService.getUser(email);

        assertNotNull(userDto);
        assertEquals(userEntity.getFirstName(), userDto.getFirstName());

    }

    @Test
    void whenNoUserThrowException() {
        when(userRepository.findByEmail(anyString())).thenReturn(null);

        assertThrows(UsernameNotFoundException.class, () -> userService.getUser(email));
    }

    @Test
    void whenCreateUser() {
        when(userRepository.findByEmail(anyString())).thenReturn(null);
        when(utils.generateAddressId(anyInt())).thenReturn(addressId);
        when(utils.generateUserId(anyInt())).thenReturn(userId);
        when(utils.generateEmailVerificationToken(anyString())).thenReturn(emailVerificationToken);
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);
        when(bCryptPasswordEncoder.encode(anyString())).thenReturn(encryptedPassword);
        Mockito.doNothing().when(amazonSES).verifyEmail(any(UserDto.class));

        UserDto userDto = new UserDto();
        userDto.setAddresses(getAddressesDto());
        userDto.setFirstName(userFirstName);
        userDto.setLastName(userLastName);
        userDto.setPassword(userPassword);
        userDto.setEmail(email);

        UserDto storedUserDetails = userService.createUser(userDto);

        assertNotNull(storedUserDetails);
        assertEquals(userEntity.getFirstName(), storedUserDetails.getFirstName());
        assertEquals(userEntity.getLastName(), storedUserDetails.getLastName());
        assertNotNull(userEntity.getUserId());

        assertEquals(storedUserDetails.getAddresses().size(), userEntity.getAddresses().size());
        verify(utils, times(2)).generateAddressId(30);
        verify(bCryptPasswordEncoder, times(1)).encode(userPassword);
        verify(userRepository, times(1)).save(any(UserEntity.class));
    }

    @Test
    final void whenCreateUserButRecordAlreadyExistsShouldThrowException() {
        when(userRepository.findByEmail(anyString())).thenReturn(userEntity);

        UserDto userDto = new UserDto();
        userDto.setAddresses(getAddressesDto());
        userDto.setFirstName(userFirstName);
        userDto.setLastName(userLastName);
        userDto.setPassword(userPassword);
        userDto.setEmail(email);

        assertThrows(UserServiceException.class, () -> userService.createUser(userDto));
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

    private List<AddressEntity> getAddressesEntity() {
        List<AddressDto> addresses = getAddressesDto();
        Type lustType = new TypeToken<List<AddressEntity>>() {}.getType();

        return new ModelMapper().map(addresses, lustType);
    }
}