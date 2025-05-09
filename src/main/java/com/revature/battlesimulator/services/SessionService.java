package com.revature.battlesimulator.services;

import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.revature.battlesimulator.dtos.responses.UserSessionResponse;

import com.revature.battlesimulator.models.User;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SessionService {

    private static final String ACTIVE_USER_ATTRIBUTE = "USER_SESSION";

    public UserSessionResponse startUserSession(User user) {
        HttpSession session = getOrCreateSession(true);
        UserSessionResponse userData = new UserSessionResponse(user);
        session.setAttribute(ACTIVE_USER_ATTRIBUTE, userData);
        return userData;
    }

    public void endUserSession() {
        HttpSession session = getOrCreateSession(false);
        if (session != null) {
            session.invalidate();
        }
    }

    public UserSessionResponse getActiveUserSession() {
        HttpSession session = getOrCreateSession(false);
        if (session == null) {
            return null;
        }
        return (UserSessionResponse) session.getAttribute(ACTIVE_USER_ATTRIBUTE);
    }

    private HttpSession getOrCreateSession(boolean create) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest();
        return request.getSession(create);
    }
}
