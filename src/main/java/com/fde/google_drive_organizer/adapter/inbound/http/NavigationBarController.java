package com.fde.google_drive_organizer.adapter.inbound.http;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Map;

@ControllerAdvice
public class NavigationBarController {

    @ModelAttribute("navAuthenticated")
    public boolean addAuthenticationStatus(@AuthenticationPrincipal OAuth2User user) {
        return user != null;
    }

    @ModelAttribute("navDisplayName")
    public String addDisplayName(@AuthenticationPrincipal OAuth2User user) {
        if (user == null) {
            return null;
        }

        Map<String, Object> attributes = user.getAttributes();
        String displayName = (String) attributes.getOrDefault("name", attributes.get("given_name"));
        if (displayName == null || displayName.isBlank()) {
            displayName = user.getName();
        }
        return displayName;
    }
}
