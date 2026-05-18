package com.hospitalmanagement.security;

import com.hospitalmanagement.model.StaffUser;
import com.hospitalmanagement.repository.StaffUserRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenService {
    private static final long TOKEN_TTL_SECONDS = 60L * 60L * 12L;

    private final StaffUserRepository staffUserRepository;
    private final Map<String, SessionRecord> sessions = new ConcurrentHashMap<>();

    public TokenService(StaffUserRepository staffUserRepository) {
        this.staffUserRepository = staffUserRepository;
    }

    public String issue(StaffUser user) {
        String token = UUID.randomUUID().toString() + UUID.randomUUID();
        sessions.put(token, new SessionRecord(user.getId(), Instant.now().plusSeconds(TOKEN_TTL_SECONDS)));
        return token;
    }

    public Optional<StaffUser> resolve(String token) {
        SessionRecord record = sessions.get(token);
        if (record == null || record.expiresAt().isBefore(Instant.now())) {
            sessions.remove(token);
            return Optional.empty();
        }
        return staffUserRepository.findById(record.userId());
    }

    private record SessionRecord(Long userId, Instant expiresAt) {
    }
}
