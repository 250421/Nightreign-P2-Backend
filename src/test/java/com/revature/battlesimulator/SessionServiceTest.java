package com.revature.battlesimulator;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.MockedStatic;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.revature.battlesimulator.dtos.responses.UserSessionResponse;
import com.revature.battlesimulator.models.User;
import com.revature.battlesimulator.services.SessionService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

class SessionServiceTest {

    private SessionService sessionService;
    private HttpServletRequest mockRequest;
    private HttpSession mockSession;
    private ServletRequestAttributes mockRequestAttributes;
    private MockedStatic<RequestContextHolder> requestContextHolderMockedStatic;

    @BeforeEach
    void setUp() {
        sessionService = new SessionService();

        mockRequest = mock(HttpServletRequest.class);
        mockSession = mock(HttpSession.class);
        mockRequestAttributes = mock(ServletRequestAttributes.class);

        when(mockRequestAttributes.getRequest()).thenReturn(mockRequest);
        requestContextHolderMockedStatic = mockStatic(RequestContextHolder.class);
        requestContextHolderMockedStatic.when(RequestContextHolder::currentRequestAttributes)
                .thenReturn(mockRequestAttributes);
    }

    @AfterEach
    void tearDown() {
        requestContextHolderMockedStatic.close();
    }

    @Test
    void testStartUserSession_storesUserInSession() {
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("testUsername");

        when(mockRequest.getSession(true)).thenReturn(mockSession);

        UserSessionResponse response = sessionService.startUserSession(mockUser);

        assertNotNull(response);
        assertEquals("testUsername", response.getUsername());
        verify(mockSession).setAttribute(eq("USER_SESSION"), any(UserSessionResponse.class));
    }

    @Test
    void testEndUserSession_invalidatesSessionIfExists() {
        when(mockRequest.getSession(false)).thenReturn(mockSession);
        sessionService.endUserSession();
        verify(mockSession).invalidate();
    }

    @Test
    void testEndUserSession_doesNothingIfNoSession() {
        when(mockRequest.getSession(false)).thenReturn(null);

        assertDoesNotThrow(() -> sessionService.endUserSession());
    }

    @Test
    void testGetActiveUserSession_returnsSessionData() {
        UserSessionResponse userResponse = new UserSessionResponse();
        userResponse.setUsername("testUsername");

        when(mockRequest.getSession(false)).thenReturn(mockSession);
        when(mockSession.getAttribute("USER_SESSION")).thenReturn(userResponse);

        UserSessionResponse result = sessionService.getActiveUserSession();

        assertNotNull(result);
        assertEquals("testUsername", result.getUsername());
    }

    @Test
    void testGetActiveUserSession_returnsNullWhenNoSession() {
        when(mockRequest.getSession(false)).thenReturn(null);
        UserSessionResponse result = sessionService.getActiveUserSession();
        assertNull(result);
    }

    @Test
    void testGetActiveUserSession_returnsNullWhenNoAttribute() {
        when(mockRequest.getSession(false)).thenReturn(mockSession);
        when(mockSession.getAttribute("USER_SESSION")).thenReturn(null);
        UserSessionResponse result = sessionService.getActiveUserSession();
        assertNull(result);
    }
}
