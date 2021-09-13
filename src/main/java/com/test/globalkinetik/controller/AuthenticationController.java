package com.test.globalkinetik.controller;

import com.test.globalkinetik.config.jwt.JwtTokenUtil;
import com.test.globalkinetik.dto.UserDto;
import com.test.globalkinetik.exception.CustomExceptionObject;
import com.test.globalkinetik.model.AuthenticationResponse;
import com.test.globalkinetik.model.Users;
import com.test.globalkinetik.repository.UserRepository;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Date;

/**
 * @author S'phokuhle on 9/13/2021
 */
@RestController
@RequestMapping("/user")
@SwaggerDefinition(
        info = @Info(
                description = "User Authentication Services",
                version = "1.0.0",
                title = "User Authentication Services"
        )
)
@Slf4j
public class AuthenticationController {
    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @ApiOperation(value = "Login with your credentials",
            notes = "Login and get the authentication token from response body/response header and user id as a response")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully logged in", response = AuthenticationResponse.class),
            @ApiResponse(code = 401, message = "Unauthorized (Incorrect credentials)", response = CustomExceptionObject.class),
            @ApiResponse(code = 400, message = "Bad Request", response = CustomExceptionObject.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = CustomExceptionObject.class)
    })
    @PostMapping(path = "/login", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AuthenticationResponse> login(@ApiParam(value = "User Credentials") @RequestBody @Valid UserDto userDto) {
        try {
            Authentication authenticate = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(userDto.getUsername(), userDto.getPassword()));

            Users user = (Users) authenticate.getPrincipal();
            String token = jwtTokenUtil.generateAccessToken(user);
            user.setActiveDate(new Date());
            user.setLoggedIn(true);
            userRepository.save(user);
            return ResponseEntity.ok()
                    .header(HttpHeaders.AUTHORIZATION, token)
                    .body(new AuthenticationResponse(String.valueOf(user.getId()), token));
        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @ApiOperation(value = "Logout of the current session",
            notes = "Takes the current token from the request header and invalidate the current session")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully logged out", response = AuthenticationResponse.class),
            @ApiResponse(code = 401, message = "Unauthorized (Invalid token)", response = CustomExceptionObject.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = CustomExceptionObject.class)
    })
    @PostMapping(path = "/logout/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity logout(@ApiParam("User id")@PathVariable("id") Long id, @ApiParam("Current user token")@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        log.info("Invalidating: {}", token);
        Users user = userRepository.getById(id);
        //Setting last active date and loggedIn to false.
        user.setActiveDate(new Date());
        user.setLoggedIn(false);
        userRepository.save(user);
        String expiredToken = jwtTokenUtil.invalidateToken(token);
        AuthenticationResponse authenticationResponse = new AuthenticationResponse();
        authenticationResponse.setToken(expiredToken.replaceFirst("Bearer ", ""));
        return ResponseEntity.ok()
                    .body(authenticationResponse);
    }
}
