package com.revature.battlesimulator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.revature.battlesimulator.models.User;
import com.revature.battlesimulator.repositories.UserRepository;
import com.revature.battlesimulator.services.UserService;


@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    private final String username = "testUsername";
    private final String rawPassword = "P@ssw0rd";

    private User user;

    @BeforeEach
    void setUp() {
        user = new User("testUsername", "P@ssw0rd");
        user.setId(2L);
    }

    @Test
    void findByAccountId_ValidId() {
        when(userRepository.findById(2L)).thenReturn((user));
        User result = userService.findByUserId(2L);
        assertEquals(2L, result.getId());
    }

    @Test
    void findByAccountId_InValidId() {
        user.setId((long) 3);
        when(userRepository.findById(2)).thenReturn(user);
        User result = userService.findByUserId(2);
        assertNotEquals(2, result.getId());
    }

    @Test
    void findByUsername_validUsername() {
        when(userRepository.findByUsernameIgnoreCase("testUsername")).thenReturn(user);
        User test = userService.findByUsername("testUsername");
        assertEquals("testUsername", test.getUsername());
    }

    @Test
    void findByUsername_NonExistentUsername() {
        user.setUsername("testUsername2");
        when(userRepository.findByUsernameIgnoreCase("testUsername")).thenReturn(user);
        User test = userService.findByUsername("testUsername");
        assertNotEquals("test@gmail.com", test.getUsername());
    }

    @Test
    void findByUsername_NullUsername() {
        user.setUsername(null);
        when(userRepository.findByUsernameIgnoreCase("testUsername")).thenReturn(user);
        User test = userService.findByUsername("testUsername");
        assertNotEquals("test@gmail.com", test.getUsername());
        assertNull(userService.findByUsername(user.getUsername()));
    }

    @Test
    void registersNewUser() {
        User newUser = new User();
        newUser.setUsername("testUsername2");
        newUser.setPassword("P@ssw0rd1");

        when(passwordEncoder.encode("P@ssw0rd1")).thenReturn("hashedPassword");

        userService.registerNewUser(newUser);

        assertEquals("testUsername2", newUser.getUsername());
        assertEquals("hashedPassword", newUser.getPassword());

        verify(userRepository).save(newUser);
    }

    @Test
    void testLogin_ValidCredentials() {
        //set hashed password
        user.setPassword("hashedPassword123");

        when(userRepository.findByUsernameIgnoreCase(username)).thenReturn(user);
        when(passwordEncoder.matches(rawPassword, "hashedPassword123")).thenReturn(true);
        User result = userService.login(username, rawPassword);

        assertNotNull(result);
        assertEquals(username, result.getUsername());

        verify(userRepository).findByUsernameIgnoreCase(username);
        verify(passwordEncoder).matches(rawPassword, "hashedPassword123");
    }
}