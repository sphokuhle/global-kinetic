package com.test.globalkinetik.controller;

import com.test.globalkinetik.config.jwt.JwtTokenUtil;
import com.test.globalkinetik.dto.UserDto;
import com.test.globalkinetik.dto.UsersDto;
import com.test.globalkinetik.exception.CustomExceptionObject;
import com.test.globalkinetik.model.AuthenticationResponse;
import com.test.globalkinetik.model.Users;
import com.test.globalkinetik.repository.UserRepository;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author S'phokuhle on 9/13/2021
 */
@RestController
@RequestMapping("/users")
@Slf4j
@SwaggerDefinition(
        info = @Info(
                description = "User Application Services",
                version = "1.0.0",
                title = "User Management Services"
        )
)
public class UserController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @ApiOperation(value = "Health test check")
    @GetMapping("health/test")
    public String healthCheck() {
        return UserController.class.getCanonicalName();
    }

    @ApiOperation(value = "Persist user details into the database")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully added a user into the database", response = UserDto.class),
            @ApiResponse(code = 400, message = "Bad Request", response = CustomExceptionObject.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = CustomExceptionObject.class)
    })
    @PutMapping
    public ResponseEntity<UserDto> addUser(@ApiParam("New user's details") @RequestBody @Valid UserDto userDto) {
        log.info("Inside add user...");
        Optional<Users> existingUser = userRepository.findByUsername(userDto.getUsername());
        if(existingUser.isPresent()) {
            throw new IllegalArgumentException(String.format("Username %s already exist", existingUser.get().getUsername()));
        }
        userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
        Users newUser = userRepository.save(new Users(userDto));
        return ResponseEntity.ok().body(new UserDto(newUser));
    }

    @ApiOperation(value = "Get a list of all users from the database")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved users from the database", response = UserDto.class),
            @ApiResponse(code = 401, message = "Unauthorized (Invalid token)", response = CustomExceptionObject.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = CustomExceptionObject.class)
    })
    @GetMapping
    public ResponseEntity<UsersDto> getUsers() {
        log.info("Inside get users...");
        List<UserDto> users = userRepository.findAllOrderById();
        return ResponseEntity.ok()
                .body(new UsersDto(users));
    }

    @ApiOperation(value = "Get a list of users with active tokens(user who are still logged in)")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved a list of logged in users from the database", response = UserDto.class),
            @ApiResponse(code = 401, message = "Unauthorized (Invalid token)", response = CustomExceptionObject.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = CustomExceptionObject.class)
    })
    @GetMapping(path = "/active", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<UserDto>> getActiveUsers() {
        List<UserDto> userDtoList = new ArrayList<>();
        //Getting the list of active users
        List<Users> users = userRepository.findByLoggedInTrue();
        //Get the current logged in user
        Users loggedInUser = (Users)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(!users.isEmpty()) {
            for(Users user: users) {
                userDtoList.add(new UserDto(user));
            }
        }
        return ResponseEntity.ok()
                //Generate a new token for the currently logged in user
                .header(HttpHeaders.AUTHORIZATION, jwtTokenUtil.generateAccessToken(loggedInUser))
                .body(userDtoList);
    }
}
