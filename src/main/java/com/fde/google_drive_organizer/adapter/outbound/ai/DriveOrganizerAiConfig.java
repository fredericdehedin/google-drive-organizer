package com.fde.google_drive_organizer.adapter.outbound.ai;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "drive-organizer.ai")
public record DriveOrganizerAiConfig(
        String baseUrl,
        String apiKey,
        String model,
        @DefaultValue("classpath:prompts/suggest-target-folder-command.md") String commandPromptPath,
        @DefaultValue("classpath:prompts/folder-structure.md") String folderStructurePromptPath
) {}
