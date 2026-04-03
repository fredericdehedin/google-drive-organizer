package com.fde.google_drive_organizer.adapter.outbound.ai;

import com.fde.google_drive_organizer.application.port.outbound.SuggestedTargetFolderRepository;
import com.fde.google_drive_organizer.domain.drive_file.document_content.DriveFileDocumentContent;
import com.fde.google_drive_organizer.domain.drive_file.ref.DriveFileRef;
import com.fde.google_drive_organizer.domain.suggest_target_folder_progress.ProgressStep;
import com.fde.google_drive_organizer.domain.suggest_target_folder_progress.SuggestTargetFolderProgressPublisher;
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
    private final SuggestTargetFolderProgressPublisher publisher;

    public SpringAiSuggestedTargetFolderRepository(ChatModel chatModel, DriveOrganizerAiConfig config, ResourceLoader resourceLoader, SuggestTargetFolderProgressPublisher publisher) {
        this.chatModel = chatModel;
        this.config = config;
        this.resourceLoader = resourceLoader;
        this.publisher = publisher;
    }

    @Override
    public String suggestTargetFolder(DriveFileRef driveFileRef, DriveFileDocumentContent content) {
        String folderStructure = loadResource(config.folderStructurePromptPath());
        String commandTemplate = loadResource(config.commandPromptPath());
        String prompt = commandTemplate
                .replace("{file}", driveFileRef.name().value())
                .replace("{content}", content.textContent().value())
                .replace("{folder-structure}", folderStructure);
        publisher.publish(driveFileRef.id(), ProgressStep.ANALYZING, "Analyzing with AI...");
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
