package com.fde.google_drive_organizer.adapter.outbound.ai;

import com.fde.google_drive_organizer.domain.model.DocumentContent;
import com.fde.google_drive_organizer.domain.model.DocumentContentTestFixture;
import com.fde.google_drive_organizer.domain.model.DriveFile;
import com.fde.google_drive_organizer.domain.model.DriveFileTestFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.core.io.DefaultResourceLoader;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SpringAiSuggestedTargetFolderRepositoryTest {

    @Mock
    private ChatModel chatModel;

    private SpringAiSuggestedTargetFolderRepository repository;

    @BeforeEach
    void setUp() {
        DriveOrganizerAiConfig config = new DriveOrganizerAiConfig(
                "https://api.example.com",
                "/v1",
                "test-key",
                "gpt-4o",
                "classpath:prompts/test-suggest-target-folder-command.md",
                "classpath:prompts/test-folder-structure.md"
        );
        repository = new SpringAiSuggestedTargetFolderRepository(chatModel, config, new DefaultResourceLoader());
    }

    @Test
    void shouldSubstitutePlaceholdersAndCallChatModel() {
        DriveFile driveFile = DriveFileTestFixture.aDriveFile()
                .withId("file-123")
                .withName("salary-certificate-2025.pdf")
                .build();
        DocumentContent content = DocumentContentTestFixture.aDocumentContent()
                .withFileId("file-123")
                .withTextContent("Salary certificate for year 2025")
                .build();
        String expectedPrompt = "File: salary-certificate-2025.pdf\n" +
                "Content: Salary certificate for year 2025\n" +
                "Folders: Taxes/2025/";
        when(chatModel.call(expectedPrompt)).thenReturn("Taxes/2025/02_Income");

        String result = repository.suggestTargetFolder(driveFile, content);

        assertThat(result).isEqualTo("Taxes/2025/02_Income");
        verify(chatModel).call(expectedPrompt);
    }

    @Test
    void shouldTrimWhitespaceFromModelResponse() {
        DriveFile driveFile = DriveFileTestFixture.aDriveFile()
                .withId("file-456")
                .withName("bank-statement.pdf")
                .build();
        DocumentContent content = DocumentContentTestFixture.aDocumentContent()
                .withFileId("file-456")
                .withTextContent("Bank account statement December 2025")
                .build();
        String expectedPrompt = "File: bank-statement.pdf\n" +
                "Content: Bank account statement December 2025\n" +
                "Folders: Taxes/2025/";
        when(chatModel.call(expectedPrompt)).thenReturn("  Taxes/2025/03_Assets  \n");

        String result = repository.suggestTargetFolder(driveFile, content);

        assertThat(result).isEqualTo("Taxes/2025/03_Assets");
    }
}
