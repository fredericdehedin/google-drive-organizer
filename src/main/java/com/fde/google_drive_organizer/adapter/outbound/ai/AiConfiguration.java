package com.fde.google_drive_organizer.adapter.outbound.ai;

import com.fde.google_drive_organizer.application.port.outbound.SuggestedTargetFolderRepository;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.model.ApiKey;
import org.springframework.ai.model.SimpleApiKey;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;

@Configuration
public class AiConfiguration {

    @Bean
    public ChatModel chatModel(DriveOrganizerAiConfig config) {

        ApiKey apiKey = new SimpleApiKey(config.apiKey());
        OpenAiApi openAiApi = OpenAiApi.builder()
                .apiKey(apiKey)
                .baseUrl(config.baseUrl())
                .completionsPath(config.completionsPath())
                .build();
        OpenAiChatOptions options = OpenAiChatOptions.builder()
                .model(config.model())
                .build();
        return OpenAiChatModel.builder()
                .openAiApi(openAiApi)
                .defaultOptions(options)
                .build();
    }

    @Bean
    public SuggestedTargetFolderRepository suggestedTargetFolderRepository(
            ChatModel chatModel,
            DriveOrganizerAiConfig config,
            ResourceLoader resourceLoader
    ) {
        return new SpringAiSuggestedTargetFolderRepository(chatModel, config, resourceLoader);
    }
}
