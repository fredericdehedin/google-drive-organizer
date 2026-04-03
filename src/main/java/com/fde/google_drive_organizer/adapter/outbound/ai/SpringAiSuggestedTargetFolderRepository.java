package com.fde.google_drive_organizer.adapter.outbound.ai;

import com.fde.google_drive_organizer.application.port.outbound.SuggestedTargetFolderRepository;
import com.fde.google_drive_organizer.domain.model.DocumentContent;
import com.fde.google_drive_organizer.domain.model.DriveFile;
import com.fde.google_drive_organizer.progress.FileId;
import com.fde.google_drive_organizer.progress.ProgressEventPublisher;
import com.fde.google_drive_organizer.progress.ProgressStep;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

public class SpringAiSuggestedTargetFolderRepository implements SuggestedTargetFolderRepository {

    private final ChatModel chatModel;
    private final DriveOrganizerAiConfig config;
    private final ResourceLoader resourceLoader;
    private final ProgressEventPublisher publisher;

    public SpringAiSuggestedTargetFolderRepository(ChatModel chatModel, DriveOrganizerAiConfig config, ResourceLoader resourceLoader, ProgressEventPublisher publisher) {
        this.chatModel = chatModel;
        this.config = config;
        this.resourceLoader = resourceLoader;
        this.publisher = publisher;
    }

    @Override
    public String suggestTargetFolder(DriveFile driveFile, DocumentContent content) {
        String folderStructure = loadResource(config.folderStructurePromptPath());
        String commandTemplate = loadResource(config.commandPromptPath());
        String prompt = commandTemplate
                .replace("{file}", driveFile.name())
                .replace("{content}", content.textContent())
                .replace("{folder-structure}", folderStructure);
        publisher.publish(new FileId(driveFile.id()), ProgressStep.ANALYZING, "Analyzing with AI...");
        return chatModel.call(prompt).trim();
    }

    private String loadResource(String path) {
        try {
            Resource resource = resourceLoader.getResource(path);
            return resource.getContentAsString(StandardCharsets.UTF_8).replace("\r\n", "\n");
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to load prompt from: " + path, e);
        }
    }
}
