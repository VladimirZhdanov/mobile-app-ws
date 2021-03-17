package com.homel.project.app.ws.io.repositories;

import com.homel.project.app.ws.io.entity.AddressEntity;
import com.homel.project.app.ws.io.entity.UserEntity;
import com.homel.project.app.ws.shared.dto.AddressDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    static boolean recordsCreated = false;

    UserEntity userEntity1;
    UserEntity userEntity2;
    String userId = "testUserId";
    String userId2 = "testUserId2";
    String encryptedPassword = "userPassword";
    String emailVerificationToken = "emailToken";
    String addressId = "AddressId";
    String userFirstName = "Vlad";
    String userLastName = "God";
    String addressType = "shipping";
    String userPassword = "password";
    String email1 = "test1@test.ru";
    String email2 = "test2@test.ru";

    @BeforeEach
    void setUp() {
        if (!recordsCreated) {
            addFakeUsers();
            recordsCreated = true;
        }
    }

    @Test
    final void testGetVerifiedUsers() {

        Pageable pageableRequest = PageRequest.of(0, 2);
        Page<UserEntity> pages = userRepository.findAllUsersWithConfirmedEmailAddress(pageableRequest);

        assertNotNull(pages);
        assertEquals(1, pages.getContent().size());

    }

    @Test
    final void testFindUserByFirstName() {
        List<UserEntity> users = userRepository.findUserByFirstName(userFirstName);

        assertNotNull(users);
        assertEquals(1, users.size());
        assertEquals(userFirstName, users.get(0).getFirstName());
    }

    @Test
    final void testFindUserByLastName() {
        List<UserEntity> users = userRepository.findUserByLastName(userLastName);

        assertNotNull(users);
        assertEquals(2, users.size());
        assertEquals(userLastName, users.get(0).getLastName());
    }

    @Test
    final void testFindUserByKeyword() {
        List<UserEntity> users = userRepository.findUserByKeyword("Fa");

        assertNotNull(users);
        assertEquals(1, users.size());
        assertEquals("Fake", users.get(0).getFirstName());
    }

    @Test
    final void testFindUserFirstNameAndLastNameByKeyword() {
        List<Object[]> users = userRepository.findUserFirstNameAndLastNameByKeyword("Fa");

        assertNotNull(users);
        assertEquals(1, users.size());

        Object[] user = users.get(0);
        String userFirstName = String.valueOf(user[0]);
        String userLastName = String.valueOf(user[1]);
        assertNotNull(userFirstName);
        assertNotNull(userLastName);

        assertEquals("Fake", userFirstName);
        assertEquals("God", userLastName);
    }

    @Test
    final void testUpdateUserEmailVerificationStatus() {
        userRepository.updateUserEmailVerificationStatus(false, userId);
        UserEntity user = userRepository.findByUserId(userId);

        assertNotNull(user);

        assertEquals(false, user.getEmailVerificationStatus());

        userRepository.updateUserEmailVerificationStatus(true, userId);

        user = userRepository.findByUserId(userId);

        assertNotNull(user);

        assertEquals(true, user.getEmailVerificationStatus());
    }

    @Test
    final void testFindUserEntityByUserId() {
        UserEntity user = userRepository.findUserEntityByUserId(userId);

        assertNotNull(user);
        assertEquals(userFirstName, user.getFirstName());
    }

    @Test
    final void testFindUserFirstNameByUserId() {
        List<Object[]> users = userRepository.findUserFirstNameByUserId(userId);
        Object[] user = users.get(0);
        String firstNameLocal = String.valueOf(user[0]);

        assertNotNull(firstNameLocal);
        assertEquals(userFirstName, firstNameLocal);
    }

    @Test
    final void testUpdateUserEmailVerificationStatusByUserId() {
        userRepository.updateUserEmailVerificationStatusByUserId(false, userId);
        UserEntity user = userRepository.findByUserId(userId);

        assertNotNull(user);

        assertEquals(false, user.getEmailVerificationStatus());

        userRepository.updateUserEmailVerificationStatusByUserId(true, userId);

        user = userRepository.findByUserId(userId);

        assertNotNull(user);

        assertEquals(true, user.getEmailVerificationStatus());
    }

    private List<AddressDto> getAddressesDto() {
        AddressDto addressDto = new AddressDto();
        addressDto.setType(addressType);
        addressDto.setCity("Vancouver");
        addressDto.setCountry("Canada");
        addressDto.setPostalCode("AVVCCC");
        addressDto.setStreetName("123 Street name");
        addressDto.setAddressId("testAddressId");

        AddressDto billingAddressDto = new AddressDto();

        billingAddressDto.setAddressId("testAddressId");
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

    private void addFakeUsers() {
        userEntity1 = new UserEntity();
        userEntity1.setFirstName(userFirstName);
        userEntity1.setLastName(userLastName);
        userEntity1.setUserId(userId);
        userEntity1.setEmail(email1);
        userEntity1.setEncryptedPassword(encryptedPassword);
        userEntity1.setEmailVerificationStatus(true);
        userEntity1.setAddresses(getAddressesEntity());

        userEntity2 = new UserEntity();
        userEntity2.setFirstName("Fake");
        userEntity2.setLastName(userLastName);
        userEntity2.setUserId(userId2);
        userEntity2.setEmail(email2);
        userEntity2.setEncryptedPassword(encryptedPassword);
        userEntity2.setEmailVerificationStatus(false);
        userEntity2.setAddresses(getAddressesEntity());

        userRepository.save(userEntity1);
        userRepository.save(userEntity2);
    }


}