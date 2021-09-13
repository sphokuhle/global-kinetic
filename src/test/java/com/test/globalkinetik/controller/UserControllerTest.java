package com.test.globalkinetik.controller;

import com.test.globalkinetik.dto.UserDto;
import com.test.globalkinetik.model.Users;
import com.test.globalkinetik.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@RunWith(JUnitPlatform.class)
public class UserControllerTest {
    @InjectMocks
    UserController userController;

    @Mock
    UserRepository userRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @Test
    public void testAddUser()
    {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        UserDto userDto = new UserDto();
        userDto.setPassword("testPassword");
        userDto.setUsername("testUser");
        userDto.setPhone("0999999999");
        Users user = new Users();
        user.setId(1L);
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setPhone(userDto.getPhone());
        user.setUsername(userDto.getUsername());

        when(userRepository.findByUsername(userDto.getUsername())).thenReturn(Optional.empty());

        when(userRepository.save(any(Users.class))).thenReturn(user);

        UserDto responseEntity = userController.addUser(userDto).getBody();
        assertThat(responseEntity.getId()).isEqualTo(String.valueOf(1));
        assertThat(responseEntity.getPhone()).isEqualTo(userDto.getPhone());
    }

    @Test
    public void testGetUsers() {
        List<UserDto> mockedUsersDatabaseTable = Arrays.asList(new UserDto("1", "user1", "0111", null),
                new UserDto("2", "user2", "0222", null),
                new UserDto("3", "user3", "0333", "pswd3"));
        when(userRepository.findAllOrderById()).thenReturn(mockedUsersDatabaseTable);

        List<UserDto> results = userController.getUsers().getBody().getUsers();
        //Checking that all database records are returned
        assertThat(results.size()).isEqualTo(mockedUsersDatabaseTable.size());
        //Checking that the password isn't returned
        assertThat(results.get(0).getPassword()).isNull();
    }
}
