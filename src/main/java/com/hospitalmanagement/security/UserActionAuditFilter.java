package com.hospitalmanagement.security;

import com.hospitalmanagement.model.ActivityLog;
import com.hospitalmanagement.model.StaffUser;
import com.hospitalmanagement.repository.ActivityLogRepository;
import com.hospitalmanagement.repository.StaffUserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class UserActionAuditFilter extends OncePerRequestFilter {
    private final ActivityLogRepository activityLogs;
    private final StaffUserRepository staffUsers;

    public UserActionAuditFilter(ActivityLogRepository activityLogs, StaffUserRepository staffUsers) {
        this.activityLogs = activityLogs;
        this.staffUsers = staffUsers;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        filterChain.doFilter(request, response);
        if (!request.getRequestURI().startsWith("/api/")
                || ("GET".equalsIgnoreCase(request.getMethod()) && response.getStatus() < HttpServletResponse.SC_BAD_REQUEST)) {
            return;
        }
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getName() == null) {
            return;
        }
        staffUsers.findByEmailIgnoreCase(auth.getName()).ifPresent(actor -> log(request, response, actor));
    }

    private void log(HttpServletRequest request, HttpServletResponse response, StaffUser actor) {
        ActivityLog log = new ActivityLog();
        log.setActor(actor);
        log.setAction(request.getMethod() + " " + request.getRequestURI());
        log.setDetails("status=" + response.getStatus());
        activityLogs.save(log);
    }
}
