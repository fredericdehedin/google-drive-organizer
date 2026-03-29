package com.fde.google_drive_organizer.adapter.outbound.ai;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = DriveOrganizerAiConfigTest.TestConfig.class)
@TestPropertySource(properties = {
        "drive-organizer.ai.base-url=https://api.example.com/v1",
        "drive-organizer.ai.completions-path=/chat/completions",
        "drive-organizer.ai.api-key=test-key",
        "drive-organizer.ai.model=gpt-4o"
})
class DriveOrganizerAiConfigTest {

    @EnableConfigurationProperties(DriveOrganizerAiConfig.class)
    static class TestConfig {}

    @Autowired
    private DriveOrganizerAiConfig config;

    @Test
    void shouldBindRequiredProperties() {
        assertThat(config.baseUrl()).isEqualTo("https://api.example.com/v1");
        assertThat(config.apiKey()).isEqualTo("test-key");
        assertThat(config.model()).isEqualTo("gpt-4o");
    }

    @Test
    void shouldBindCompletionsPath() {
        assertThat(config.completionsPath()).isEqualTo("/chat/completions");
    }

    @Test
    void shouldUseDefaultPromptPaths() {
        assertThat(config.commandPromptPath()).isEqualTo("classpath:prompts/suggest-target-folder-command.md");
        assertThat(config.folderStructurePromptPath()).isEqualTo("classpath:prompts/folder-structure.md");
    }
}
