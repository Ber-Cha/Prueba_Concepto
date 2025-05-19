package com.kafka.provider.springbootprovider.JWT;

import org.springframework.stereotype.Service;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ActiveTokenService {
    private final ConcurrentHashMap<String, String> activeTokens = new ConcurrentHashMap<>();

    public void addToken(String username, String token) {
        activeTokens.put(username, token);
    }

    public void removeToken(String username) {
        activeTokens.remove(username);
    }

    public boolean isUserAlreadyLoggedIn(String username) {
        return activeTokens.containsKey(username);
    }

    public boolean isTokenActive(String username, String token) {
        String activeToken = activeTokens.get(username);
        return activeToken != null && activeToken.equals(token);
    }

    public void invalidatePreviousToken(String username) {
        activeTokens.remove(username);
    }
}

