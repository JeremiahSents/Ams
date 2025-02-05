package dev.sentomero.backend_ams.service;

import dev.sentomero.backend_ams.dto.AmsUserDto;
import dev.sentomero.backend_ams.dto.AuthenticationResponse;

import java.util.List;


// File must be named AmsUserService.java
public interface AmsUserService {
    AmsUserDto savedUser(AmsUserDto user);

    AuthenticationResponse authenticateUser(String username, String password);

    AmsUserDto getUserByUsername(String username);

    List<AmsUserDto> getAllUsers();

    AmsUserDto getUserById(int id);

    AmsUserDto updateUser(int id, AmsUserDto user);

    void deleteUser(int id);
}
