package com.homel.project.app.ws.service;

import com.homel.project.app.ws.shared.dto.UserDto;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {
    UserDto createUser(UserDto userDto);
    UserDto getUser(String email);
    UserDto getUserByUserId(String id);

    UserDto updateUser(String id, UserDto userDto);

    void deleteUserById(String id);

    List<UserDto> getUsers(int page, int limit);

    boolean verifyEmailToken(String token);
}
