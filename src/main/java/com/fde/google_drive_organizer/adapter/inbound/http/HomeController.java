package com.fde.google_drive_organizer.adapter.inbound.http;

import java.util.Map;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(@AuthenticationPrincipal OAuth2User user, Model model) {
        if (user != null) {
            Map<String, Object> attributes = user.getAttributes();
            String displayName = (String) attributes.getOrDefault("name", attributes.get("given_name"));
            if (displayName == null || displayName.isBlank()) {
                displayName = user.getName();
            }
            model.addAttribute("displayName", displayName);
            model.addAttribute("authenticated", true);
        } else {
            model.addAttribute("authenticated", false);
        }

        model.addAttribute("message", "Hello HTMX from Controller");
        return "index";
    }
}