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

    private static final String USER_SESSION_KEY = "USER_SESSION";

    public UserSessionResponse createSession(User user) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest();
        HttpSession session = request.getSession(true);

        UserSessionResponse userSession = new UserSessionResponse(user);
        session.setAttribute(USER_SESSION_KEY, userSession);

        return userSession;
    }

    public UserSessionResponse getCurrentSession() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest();
        HttpSession session = request.getSession(false);

        if (session == null) {
            return null;
        }

        return (UserSessionResponse) session.getAttribute(USER_SESSION_KEY);
    }

    public void invalidateSession() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest();
        HttpSession session = request.getSession(false);

        if (session != null) {
            session.invalidate();
        }
    }
}
