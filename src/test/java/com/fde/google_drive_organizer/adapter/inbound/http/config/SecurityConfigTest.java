package com.fde.google_drive_organizer.adapter.inbound.http.config;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oauth2Login;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@ContextConfiguration(classes = {SecurityConfig.class, SecurityConfigTest.TestController.class})
@EnableWebSecurity
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private ClientRegistrationRepository clientRegistrationRepository;

    @Mock
    private OAuth2AuthorizedClientService authorizedClientService;

    @Test
    void loginShouldRedirectToOauthService() throws Exception {
        mockMvc.perform(get("/protected"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/oauth2/authorization/google"));
    }

    @Test
    void shouldDenyAccessToApiEndpointsWhenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/test"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/oauth2/authorization/google"));
    }

    @Test
    void shouldAllowAccessToApiEndpointsWhenAuthenticated() throws Exception {
        mockMvc.perform(get("/api/test")
                        .with(oauth2Login()))
                .andExpect(status().isOk());
    }

    @Test
    void logoutShouldRedirectToRoot() throws Exception {
        mockMvc.perform(post("/logout")
                        .with(csrf())
                        .with(oauth2Login()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @RestController
    public static class TestController {

        @GetMapping("/")
        public String home() {
            return "home";
        }

        @GetMapping("/api/test")
        public String protectedEndpoint() {
            return "protected";
        }
    }
}