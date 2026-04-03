package com.fde.google_drive_organizer.adapter.outbound.ai;

import com.fde.google_drive_organizer.application.port.outbound.SuggestedTargetFolderRepository;
import com.fde.google_drive_organizer.progress.ProgressEventPublisher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.core.io.DefaultResourceLoader;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class AiConfigurationTest {

    private final AiConfiguration aiConfiguration = new AiConfiguration();

    @Mock
    private ProgressEventPublisher publisher;

    private final DriveOrganizerAiConfig config = new DriveOrganizerAiConfig(
            "https://api.example.com",
            "/v1/chat/completions",
            "test-key",
            "gpt-4o",
            "classpath:prompts/suggest-target-folder-command.md",
            "classpath:prompts/folder-structure.md"
    );

    @Test
    void shouldCreateChatModel() {
        ChatModel chatModel = aiConfiguration.chatModel(config);

        assertThat(chatModel).isNotNull();
    }

    @Test
    void shouldCreateSuggestedTargetFolderRepository() {
        ChatModel chatModel = aiConfiguration.chatModel(config);

        SuggestedTargetFolderRepository repository = aiConfiguration.suggestedTargetFolderRepository(
                chatModel, config, new DefaultResourceLoader(), publisher
        );

        assertThat(repository).isNotNull().isInstanceOf(SpringAiSuggestedTargetFolderRepository.class);
    }
}
