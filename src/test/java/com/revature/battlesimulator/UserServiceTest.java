package com.revature.battlesimulator;

import com.revature.battlesimulator.models.Room;
import com.revature.battlesimulator.models.User;
import com.revature.battlesimulator.repositories.UserRepository;
import com.revature.battlesimulator.services.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;


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

    @Test
    void findByRoomId_ValidRoomId() {
        Room room1 = new Room();
        room1.setId("LLL");
        room1.setName("room1");
        room1.setMatchInProgress(false);

        User user1 = new User("user1", "password1");
        user1.setId(1L);
        user1.setRoom(room1);

        User user2 = new User("user2", "password2");
        user2.setId(2L);
        user2.setRoom(room1);

        when(userRepository.findByRoom_Id("room1")).thenReturn(Arrays.asList(user1, user2));

        List<User> result = userService.findByRoomId("room1");

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("user1", result.get(0).getUsername());
        assertEquals("user2", result.get(1).getUsername());
        verify(userRepository).findByRoom_Id("room1");
    }

    @Test
    void findByRoomId_RoomNotFound() {
        Room room1 = new Room();
        room1.setId("LLL");
        room1.setName("room1");

        User user1 = new User("user1", "password1");
        user1.setId(1L);
        user1.setRoom(room1);

        User user2 = new User("user2", "password2");
        user2.setId(2L);
        user2.setRoom(room1);

        when(userRepository.findByRoom_Id("room2")).thenReturn(Arrays.asList());

        List<User> result = userService.findByRoomId("room2");

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(userRepository).findByRoom_Id("room2");
    }

}
