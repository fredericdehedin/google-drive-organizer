package com.fde.google_drive_organizer.adapter.outbound.oauth;

import com.fde.google_drive_organizer.adapter.outbound.drive.AccessTokenProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.stereotype.Component;

/**
 * Google OAuth2 implementation of AccessTokenProvider.
 * Retrieves access tokens from Spring Security's OAuth2 authorized client service.
 */
@Component
public class GoogleOAuth2AccessTokenProvider implements AccessTokenProvider {

    private static final String CLIENT_REGISTRATION_ID = "google";
    
    private final OAuth2AuthorizedClientService authorizedClientService;

    public GoogleOAuth2AccessTokenProvider(OAuth2AuthorizedClientService authorizedClientService) {
        this.authorizedClientService = authorizedClientService;
    }

    @Override
    public String getAccessToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null) {
            return null;
        }

        OAuth2AuthorizedClient authorizedClient = authorizedClientService.loadAuthorizedClient(
                CLIENT_REGISTRATION_ID,
                authentication.getName()
        );

        if (authorizedClient == null || authorizedClient.getAccessToken() == null) {
            return null;
        }

        return authorizedClient.getAccessToken().getTokenValue();
    }
}
